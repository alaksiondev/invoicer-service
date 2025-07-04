package io.github.alaksion.invoicer.foundation.storage.remote.minIO

import io.github.alaksion.invoicer.foundation.env.secrets.SecretsProvider
import io.github.alaksion.invoicer.foundation.storage.remote.FileUploader
import io.minio.MinioClient
import io.minio.UploadObjectArgs

@Suppress("UnusedPrivateProperty")
internal class MinIOFileUploader(
    private val secretsProvider: SecretsProvider
) : FileUploader {

    override suspend fun uploadFile(
        localFilePath: String,
        fileName: String
    ): String {
        val client = MinioClient.builder()
            .endpoint("")
            .credentials(
                "",
                ""
            )
            .build()

        val uploadPayload = UploadObjectArgs.builder()
            .bucket("")
            .`object`(fileName)
            .filename(localFilePath)
            .build()

        val response = client.uploadObject(uploadPayload)

        return response.`object`()
    }
}
