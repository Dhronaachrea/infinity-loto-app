package com.skilrock.infinitylotoapp.network

import com.skilrock.infinitylotoapp.data_class.request_data_class.*
import com.skilrock.infinitylotoapp.data_class.response_data_class.*
import com.skilrock.infinitylotoapp.utility.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiCallInterface {

    @Headers(CONTENT_TYPE)
    @POST(LOGIN_URL)
    suspend fun callLoginApi(
        @Body loginRequestData: LoginRequestData
    ) : Response<LoginResponseData>


    @Headers(CONTENT_TYPE)
    @POST(REGISTRATION_URL)
    suspend fun callRegistrationApi(
        @Body registrationRequestData: RegistrationRequestData
    ) : Response<RegistrationResponseData>

    @Headers(CONTENT_TYPE)
    @POST(REGISTRATION_OTP_URL)
    suspend fun callRegistrationOtpApi(
        @Body registrationOtpRequestData: RegistrationOtpRequestData
    ) : Response<RegistrationOtpResponseData>

    @Headers(CONTENT_TYPE)
    @POST(FORGOT_PASSWORD_URL)
    suspend fun callForgotPasswordOtpApi(
        @Body forgotPasswordRequestData: ForgotPasswordRequestData
    ) : Response<ForgotPasswordResponseData>

    @Headers(CONTENT_TYPE)
    @POST(RESET_PASSWORD_URL)
    suspend fun callForgotPasswordResetApi(
        @Body forgotPasswordRequestData: ForgotPasswordRequestData
    ) : Response<ForgotPasswordResponseData>

    @Headers(CONTENT_TYPE)
    @POST(VERSION_URL)
    suspend fun callVersionApi(
        @Body versionRequestData: VersionRequestData
    ) : Response<VersionResponseData>

    @Headers(CONTENT_TYPE)
    @POST(MY_TRANSACTIONS_URL)
    suspend fun callMyTransactionsApi(
        @Body myTransactionRequestData: MyTransactionRequestData
    ) : Response<MyTransactionResponseData>

    @Headers(CONTENT_TYPE)
    @POST(MY_TICKETS_URL)
    suspend fun callMyTicketsApi(
        @Body myTicketsRequestData: MyTicketsRequestData
    ) : Response<MyTicketsResponseData>

    @Headers(CONTENT_TYPE)
    @POST(CHANGE_PASSWORD_URL)
    suspend fun callChangePasswordApi(
        @Body changePasswordRequestData: ChangePasswordRequestData
    ) : Response<ChangePasswordResponseData>

    @Headers(CONTENT_TYPE)
    @POST(UPDATE_BALANCE_URL)
    suspend fun callUpdateBalanceApi(
        @Body balanceRequestData: BalanceRequestData
    ) : Response<BalanceResponseData>

    @Headers(CONTENT_TYPE)
    @POST(FUND_TRANSFER_URL)
    suspend fun callFundTransferFromWalletApi(
        @Body fundTransferFromWalletRequestData: FundTransferFromWalletRequestData
    ) : Response<FundTransferFromWalletResponseData>

    @Headers(CONTENT_TYPE)
    @POST(LOGOUT_URL)
    suspend fun callLogoutApi(
        @Body logoutRequestData: LogoutRequestData
    ) : Response<LogoutResponseData>

    @Headers(CONTENT_TYPE)
    @POST(JACKPOT_TIMER_URL)
    suspend fun callJackpotAndTimerApi(
        @Body jackpotAndTimerRequestData: JackpotAndTimerRequestData
    ) : Response<JackpotAndTimerResponseData>

    @Multipart
    @POST(UPLOAD_AVATAR_URL)
    suspend fun callProfilePicApi(
        @PartMap paramsMap: HashMap<String, RequestBody>,
        @Part imageFile: MultipartBody.Part
    ) : Response<UploadAvatarResponseData>

    @Headers(CONTENT_TYPE)
    @POST(UPDATE_PROFILE_URL)
    suspend fun callProfileUpdateApi(
        @Body updateProfileRequestData: UpdateProfileRequestData
    ): Response<UpdateProfileResponseData>

    @Headers(CONTENT_TYPE, MERCHANT_CODE)
    @POST(DEPOSIT_UI_URL)
    suspend fun callDepositUiApi(
        @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body depositUiRequestData: DepositUiRequestData
    ) : Response<ResponseBody>

    @Headers(CONTENT_TYPE, MERCHANT_CODE)
    @POST(DEPOSIT_UI_URL)
    suspend fun callWithdrawalUiApi(
        @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body withdrawalUiRequestData: WithdrawalUiRequestData
    ) : Response<ResponseBody>

    @Headers(CONTENT_TYPE, MERCHANT_CODE)
    @POST(PENDING_TRANSACTIONS_URL)
    suspend fun callDepositPendingApi(
        @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body depositPendingRequestData: DepositPendingRequestData
    ) : Response<DepositPendingResponseData>

    @Headers(CONTENT_TYPE, MERCHANT_CODE)
    @POST(WITHDRAWAL_REQUEST_URL)
    suspend fun callWithdrawRequestApi(
        @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body withdrawalRequestData: WithdrawalRequestData
    ) : Response<WithdrawResponseData>

    @Headers(CONTENT_TYPE, MERCHANT_CODE)
    @POST(ADD_NEW_ACCOUNT_URL)
    suspend fun callAddNewAccountApi(
        @Header("playerId") playerId: String = PlayerInfo.getPlayerId().toString(),
        @Header("playerToken") playerToken: String = PlayerInfo.getPlayerToken(),
        @Body addNewAccountRequestData: AddNewAccountRequestData
    ) : Response<AddNewAccountResponseData>

    @Headers(CONTENT_TYPE)
    @POST(PLAYER_INBOX_URL)
    suspend fun callInboxApi(
        @Body playerInboxRequestData: PlayerInboxRequestData
    ) : Response<PlayerInboxResponseData>

    @Headers(CONTENT_TYPE)
    @POST(PLAYER_INBOX_ACTIVITY_URL)
    suspend fun callInboxReadApi(
        @Body playerInboxReadRequestData: PlayerInboxReadRequestData
    ) : Response<PlayerInboxReadResponseData>

    @Headers(CONTENT_TYPE)
    @POST(PLAYER_INBOX_ACTIVITY_URL)
    suspend fun callInboxDeleteApi(
        @Body playerInboxDeleteRequestData: PlayerInboxDeleteRequestData
    ) : Response<PlayerInboxResponseData>

    @Headers(CONTENT_TYPE)
    @POST(EMAIL_OTP_URL)
    suspend fun callEmailCodeApi(
        @Body emailCodeRequestData: EmailCodeRequestData
    ) : Response<EmailCodeResponseData>

    @Headers(CONTENT_TYPE)
    @POST(EMAIL_VERIFY_URL)
    suspend fun callEmailVerifyApi(
        @Body emailVerifyRequestData: EmailVerifyRequestData
    ) : Response<EmailVerifyResponseData>
}