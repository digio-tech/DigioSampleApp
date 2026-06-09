package com.test.myapplication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

/**
 * Created by akash on 16/03/26
 */
object SdkJwtGenerator {
    fun generateToken(
        clientSecret: String?, transactionId: String?,
        templateName: String?, customerIdentifier: String?,
        expirySeconds: Long
    ): String {
        return "Bearer ${JWT.create()
                .withClaim("transaction_id", transactionId)
                .withClaim("template_name", templateName)
                .withClaim("customer_identifier", customerIdentifier)
                .withExpiresAt(Date(System.currentTimeMillis() + expirySeconds * 1000))
                .sign(Algorithm.HMAC256(clientSecret))
        }"
    }
}
