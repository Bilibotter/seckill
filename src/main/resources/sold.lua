local key = KEYS[1]
local sold = tonumber(ARGV[1])
--lua的随机数不均匀
--local ttl = tonumber(ARGV[2])
local remain = tonumber(redis.call('get', key) or "0")
if (remain - sold) >= 0 then
    return redis.call('decrby', key, sold);
    --redis.call('expire', key, ttl)
end
return -1;