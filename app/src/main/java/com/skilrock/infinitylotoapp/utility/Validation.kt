package com.skilrock.infinitylotoapp.utility

sealed class ChangePasswordValidation {
    data class OldPasswordValidation(val errorMessageCode: Int) : ChangePasswordValidation()
    data class NewPasswordValidation(val errorMessageCode: Int) : ChangePasswordValidation()
    data class ConfirmPasswordValidation(val errorMessageCode: Int) : ChangePasswordValidation()
}

