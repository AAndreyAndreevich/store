package app.service;

import app.dto.StoreOperationResult;
import app.enam.StoreOperationType;
import app.entity.Account;
import app.entity.Store;
import app.handler.AccessDeniedException;
import app.handler.AlreadyExistsException;
import app.handler.InvalidInputException;
import app.handler.NotFoundException;
import app.repository.AccountRepository;
import app.repository.StoreRepository;
import app.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

@Service
public class StoreService {

    private static final Integer MAX_STORE_NAME_LENGTH = 30;
    private static final Integer MIN_STORE_NAME_LENGTH = 3;

    private static final Logger log = LoggerFactory.getLogger(StoreService.class);

    private final StoreRepository storeRepository;
    private final AccountRepository accountRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public StoreService(StoreRepository storeRepository, AccountRepository accountRepository,
                        SecurityUtils securityUtils) {
        this.storeRepository = storeRepository;
        this.accountRepository = accountRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public StoreOperationResult createStore(String storeName) {
        if (StringUtils.isEmpty(storeName)) {
            throw new InvalidInputException("Название магазина не может быть пустым");
        }
        Long accountId = securityUtils.getCurrentUserId(accountRepository);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        checkLengthName(storeName);
        if (storeRepository.existsByName(storeName)) {
            throw new AlreadyExistsException("Магазин с названием '" + storeName + "' существует");
        }
        Store store = new Store(storeName, account);
        log.info("Попытка сохранить магазин: \n {}", store);
        storeRepository.save(store);
        log.info("Магазин '{}' успешно создан пользователем '{}'", storeName, account.getUsername());
        return new StoreOperationResult(StoreOperationType.CREATE, account.getUsername(), store.getName());
    }

    @Transactional
    public StoreOperationResult changeName(String oldName, String newName) {
        if (StringUtils.isEmpty(newName)) {
            throw new InvalidInputException("Название магазина не может быть пустым");
        }
        Long accountId = securityUtils.getCurrentUserId(accountRepository);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Store store = storeRepository.findByName(oldName)
                .orElseThrow(() -> new NotFoundException("Магазин с названием '" + oldName + "' не найден"));
        checkLengthName(newName);
        if (storeRepository.existsByName(newName)) {
            throw new AlreadyExistsException("Магазин с названием '" + newName + "' существует");
        }
        if (oldName.equals(newName)) {
            throw new InvalidInputException("Название не может совпадать");
        }
        if (!store.getOwner().equals(account)) {
            throw new AccessDeniedException("Пользователю не принадлежит магазин");
        }

        store.setName(newName);
        storeRepository.save(store);

        return new StoreOperationResult(
                StoreOperationType.CHANGE_STORENAME, account.getUsername(), newName
        );
    }

    private void checkLengthName(String name) {
        if (name.length() < MIN_STORE_NAME_LENGTH || name.length() > MAX_STORE_NAME_LENGTH) {
            throw new InvalidInputException("Название магазина должно быть от " + MIN_STORE_NAME_LENGTH + " до " +
                    MAX_STORE_NAME_LENGTH + " символов");
        }
    }
}