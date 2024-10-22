package com.example.transactional

import com.example.transactional.usecase.UserUsecase
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class TransactionalApplication {

    @Bean
    fun run(userUsecase: UserUsecase) = CommandLineRunner {
        userUsecase.createUser(name = "Alice")
        println("\n")
        userUsecase.createUser(name = "Alice")
        println("\n")

        // 각 문제 상황을 테스트하는 코드
        userUsecase.executeLazyLoadingIssue(1L)
        println("\n")
        userUsecase.executeLazyLoadingSolution(1L)
        println("\n")
        userUsecase.executeConcurrencyIssue(1L)
        println("\n")
        userUsecase.executeConcurrencySolution(1L)
        println("\n")
        userUsecase.executeReadOnlyTransactionUpdate(1L, "ReadOnly Name")
        println("\n")
        userUsecase.executeDirectMethodCall(1L, "Direct Call Name")
        println("\n")
        userUsecase.executeNestedTransaction(1L, "Nested Name")
        println("\n")
        userUsecase.executeIsolationLevelMismatch(1L)
        println("\n")

        // 예외 처리 테스트 코드 추가
        userUsecase.executeCreateUserWithCheckedException(1L)
        println("\n")
        userUsecase.executeCreateUserWithUncheckedException(1L)
        println("\n")
    }
}

fun main(args: Array<String>) {
    runApplication<TransactionalApplication>(*args)
}
