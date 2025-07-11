package foundation.cache.di

import foundation.cache.CacheHandler
import foundation.cache.redis.RedisCacheHandler
import foundation.cache.redis.RedisInstance
import foundation.cache.redis.jedis.JedisRedisManager
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val cacheDiModule = DI.Module("cacheDiModule") {
    bindProvider<CacheHandler> {
        RedisCacheHandler(
            redisInstance = instance(),
            logger = instance()
        )
    }

    bindSingleton<RedisInstance> {
        JedisRedisManager(
            secrets = instance(),
            dispatcher = Dispatchers.IO,
            logger = instance()
        )
    }
}
