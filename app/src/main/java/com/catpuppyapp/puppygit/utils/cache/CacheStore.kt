package com.catpuppyapp.puppygit.utils.cache

interface CacheStore { // TODO定时删除
    //获取一个随机的key
    fun getARandomKey():String

    //存储value到随机生成的key，然后返回key
    fun setThenReturnKey(value: Any):String

    //返回值是key关联的上个值，如果无，返回null，所以需要 Any?
    fun set(key:String, value:Any):Any?
    fun get(key:String):Any?

    /**
     * if key exists, return value, else return default
     */
    fun getOrDefault(key: String, default: ()->Any): Any

    /**
     * if key exists, return value, else will put and return default
     */
    fun getOrPut(key: String, default: ()->Any): Any


    fun<T> getByType(key:String):T?

    fun<T:Any> getOrDefaultByType(key:String, default:()->T):T

    fun<T:Any> getOrPutByType(key:String, default:()->T):T

    fun<T> getByTypeThenDel(key:String):T?

    fun del(key:String):Any?

    fun getThenDel(key:String):Any?

    //让newKey指向oldKey的数据，然后从map删除oldKey
    fun updateKey(oldKey:String, newKey:String, requireDelOldKey:Boolean)

    //清空缓存
    fun clear()

    //清除拥有某些key前缀的value，如果requireDel为true，会删除，否则会把值设为null
    fun clearByKeyPrefix(keyPrefix:String)

    fun clearByPredicate(predicate:(key:String)->Boolean)
}
