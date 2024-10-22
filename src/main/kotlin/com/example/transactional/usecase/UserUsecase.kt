package com.example.transactional.usecase

import com.example.transactional.exception.CustomCheckedException
import com.example.transactional.exception.CustomUncheckedException
import com.example.transactional.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class UserUsecase(private val userService: UserService) {

    fun createUser(name: String) {
        userService.createUser(name)
    }

    fun executeLazyLoadingIssue(userId: Long) {
        try {
            val user = userService.getUserWithLazyLoadingIssue(userId)
            println(user.name) // LazyInitializationException 발생
        } catch (e: Exception) {
            println("Lazy Loading 문제 발생: ${e.message}")
        }
    }

    fun executeLazyLoadingSolution(userId: Long) {
        userService.getUserWithLazyLoadingSolution(userId)
    }

    fun executeReadOnlyTransactionUpdate(userId: Long, newName: String) {
        try {
            userService.updateUserInReadOnlyTransaction(userId, newName)
        } catch (e: Exception) {
            println("읽기 전용 트랜잭션 문제 발생: ${e.message}")
        }
    }

    fun executeConcurrencyIssue(userId: Long) {
        runBlocking {
            val job1 = launch(Dispatchers.Default) {
                try {
                    userService.updateUserConcurrently(userId, "Alice")
                } catch (e: Exception) {
                    println("Alice 업데이트 실패: ${e.message}")
                }
            }
            val job2 = launch(Dispatchers.Default) {
                try {
                    userService.updateUserConcurrently(userId, "Bob")
                } catch (e: Exception) {
                    println("Bob 업데이트 실패: ${e.message}")
                }
            }
            job1.join()
            job2.join()
        }
        val updatedUser = userService.getUserWithLazyLoadingSolution(userId)
        println("최종 사용자 이름: ${updatedUser.name}")
    }

    fun executeConcurrencySolution(userId: Long) {
        runBlocking {
            val job1 = launch(Dispatchers.Default) {
                try {
                    userService.updateUserWithLock(userId, "Alice")
                } catch (e: Exception) {
                    println("Alice 업데이트 실패: ${e.message}")
                }
            }
            val job2 = launch(Dispatchers.Default) {
                try {
                    userService.updateUserWithLock(userId, "Bob")
                } catch (e: Exception) {
                    println("Bob 업데이트 실패: ${e.message}")
                }
            }
            job1.join()
            job2.join()
        }
        val updatedUser = userService.getUserWithLazyLoadingSolution(userId)
        println("최종 사용자 이름: ${updatedUser.name}")
    }

    fun executeDirectMethodCall(userId: Long, newName: String) {
        userService.updateUserDirectly(userId, newName)
    }

    fun executeNestedTransaction(userId: Long, newName: String) {
        userService.nestedTransactionExample(userId, newName)
    }

    fun executeIsolationLevelMismatch(userId: Long) {
        val user1 = userService.readUncommittedExample(userId)
        val user2 = userService.readCommittedExample(userId)
        println("READ_UNCOMMITTED: ${user1.name}, READ_COMMITTED: ${user2.name}")
    }

    // 체크 예외 처리 실행
    fun executeCreateUserWithCheckedException(userId: Long) {
        try {
            println(" before => ${userService.findUserById(userId).name.toString()}");
            userService.createUserWithCheckedException(userId)
        } catch (e: CustomCheckedException) {
            println("체크 예외 발생: ${e.message}")
        }
        println(" after => ${userService.findUserById(userId).name.toString()}");
    }

    // 언체크 예외 처리 실행
    fun executeCreateUserWithUncheckedException(userId: Long) {
        try {
            println(" before => ${userService.findUserById(userId).name.toString()}");
            userService.createUserWithUncheckedException(userId)
        } catch (e: CustomUncheckedException) {
            println("언체크 예외 발생: ${e.message}")
        }
        println(" after => ${userService.findUserById(userId).name.toString()}");
    }
}
