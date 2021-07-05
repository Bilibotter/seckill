--
local key = KEYS[1]
local limit = tonumber(ARGV[1])
--获取当前流量，无流量则为0
local hold = tonumber(redis.call('get', key) or "0")
if hold + 1 > limit then
    return 0;
else
    redis.call('incrby', key, 1)
    redis.call('expire', key, 2)
    return hold+1
end