package com.example.transactional.service

import com.example.transactional.entity.User
import com.example.transactional.exception.CustomCheckedException
import com.example.transactional.exception.CustomUncheckedException
import com.example.transactional.listener.TransactionEventListener
import com.example.transactional.repository.UserRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    fun createUser(name: String): User {
        TransactionEventListener.register()
        val user = User(name = name, age = 20)
        return userRepository.save(user)
    }

    fun findUserById(userId: Long): User {
        TransactionEventListener.register()
        return userRepository.findById(userId).orElseThrow()
    }

    // Lazy Loading 문제 예시
    @Transactional
    fun getUserWithLazyLoadingIssue(userId: Long): User {
        TransactionEventListener.register()
        val user = userRepository.findById(userId).orElseThrow()
        return user
    }

    // Lazy Loading 문제 해결
    @Transactional
    fun getUserWithLazyLoadingSolution(userId: Long): User {
        TransactionEventListener.register()
        val user = userRepository.findById(userId).orElseThrow()
        println(user?.name) // LazyInitializationException 발생하지 않음
        return user
    }

    // 동시성 문제 예시
    @Transactional
    fun updateUserConcurrently(userId: Long, newName: String) {
        TransactionEventListener.register()
        val user = userRepository.findById(userId).orElseThrow()
        user.name = newName
        // 동시성 문제 발생 가능
    }

    // 동시성 문제 해결
    @Transactional
    fun updateUserWithLock(userId: Long, newName: String) {
        TransactionEventListener.register()
        val user = userRepository.findById(userId).orElseThrow()
        entityManager.lock(user, LockModeType.OPTIMISTIC)
        user.name = newName
    }

    // 읽기 전용 트랜잭션 문제 예시
    @Transactional(readOnly = true)
    fun updateUserInReadOnlyTransaction(userId: Long, newName: String) {
        TransactionEventListener.register()
        val user = userRepository.findById(userId).orElseThrow()
        user.name = newName // HibernateException 발생
    }

    // 프록시 객체와 직접 접근 문제 예시
    @Transactional
    fun updateUserDirectly(userId: Long, newName: String) {
        TransactionEventListener.register()
        updateUserName(userId, newName) // 같은 클래스 내 메서드 호출
    }

    // 프록시 객체와 직접 접근 문제 예시
    @Transactional
    fun updateUserName(userId: Long, newName: String) {
        TransactionEventListener.register()
        val user = userRepository.findById(userId).orElseThrow()
        user.name = newName
    }

    // Nested 트랜잭션 문제 예시
    @Transactional(propagation = Propagation.NESTED)
    fun nestedTransactionExample(userId: Long, newName: String) {
        TransactionEventListener.register()
        val user = userRepository.findById(userId).orElseThrow()
        user.name = newName
        // 중첩 트랜잭션 문제 발생 가능
    }

    // 트랜잭션 격리 수준 불일치 문제 예시
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    fun readUncommittedExample(userId: Long): User {
        TransactionEventListener.register()
        return userRepository.findById(userId).orElseThrow()
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun readCommittedExample(userId: Long): User {
        TransactionEventListener.register()
        return userRepository.findById(userId).orElseThrow()
    }

    // 전파 문제 예시
    @Transactional(propagation = Propagation.REQUIRED)
    fun createUserWithPropagation(name: String): User {
        TransactionEventListener.register()
        val user = User(name = name, age = 25)
        return userRepository.save(user)
    }

    // 체크 예외 처리 예시
    @Transactional
    fun createUserWithCheckedException(userId: Long): User {
        TransactionEventListener.register()

        val user = findUserById(userId)
        user.name = "체크드에러"
        userRepository.save(user)
        throw CustomCheckedException("체크 예외 발생")
    }

    // 언체크 예외 처리 예시
    @Transactional
    fun createUserWithUncheckedException(userId: Long): User {
        TransactionEventListener.register()
        val user = findUserById(userId)
        user.name = "언체크드에러"
        userRepository.save(user)
        throw CustomUncheckedException("언체크 예외 발생")
    }
}
