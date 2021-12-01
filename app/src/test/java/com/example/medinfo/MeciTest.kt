package com.example.medinfo

import com.example.medinfo.entity.User
import com.example.medinfo.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class MeciTest {

    private lateinit var userRepo: UserRepository

    @Test
    fun loginTest() = runBlocking {
        userRepo = UserRepository();
        val response = userRepo.login("ramshah@gmail.com", "ramshah");
        val expectedResult: Boolean = true;
        val actualResult = response.success;
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    fun loginTestFail() = runBlocking {
        userRepo = UserRepository();
        val response = userRepo.login("ram@ram1", "ramshah");
        val expectedResult: Boolean = true;
        val actualResult = response.success;
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    fun signUpTest() = runBlocking {
        userRepo = UserRepository();
        val user = User(
            fullName = "Harish Bohara",
            email = "harish",
            phone = "9800000000",
            password = "haris"
        );
        val response = userRepo.register(user);
        val expectedResult: Boolean = true;
        val actualResult = response.success;
        Assert.assertEquals(expectedResult, actualResult);
    }

}