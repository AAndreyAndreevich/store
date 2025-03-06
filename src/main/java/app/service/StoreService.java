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
    public StoreOperationResult changeName(String storeName) {
        //смена названия для магазина
        return null;
    }

}