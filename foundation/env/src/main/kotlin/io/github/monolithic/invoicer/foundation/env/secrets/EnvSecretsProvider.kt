package io.github.monolithic.invoicer.foundation.env.secrets

import io.github.monolithic.invoicer.foundation.env.application.InvoicerEnvironment

internal class EnvSecretsProvider(
    private val environment: InvoicerEnvironment
) : SecretsProvider {

    override fun getSecret(key: SecretKeys): String {
        val path = when (key) {
            SecretKeys.DB_PASSWORD -> "database.password"
            SecretKeys.DB_USERNAME -> "database.username"
            SecretKeys.DB_URL -> "database.url"
            SecretKeys.JWT_AUDIENCE -> "jwt.audience"
            SecretKeys.JWT_ISSUER -> "jwt.issuer"
            SecretKeys.JWT_SECRET -> "jwt.secret"
            SecretKeys.JWT_REALM -> "jwt.realm"
            SecretKeys.REDIS_HOST -> "redis.host"
            SecretKeys.REDIS_PORT -> "redis.port"
            SecretKeys.KAFKA_BOOTSTRAP -> "kafka.bootstrap_servers"
            SecretKeys.FIREBASE_ID -> "firebase.project_id"
        }

        return environment.getVariable(path).orEmpty()
    }
}
