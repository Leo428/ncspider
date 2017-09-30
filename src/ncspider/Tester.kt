package ncspider

object Tester {
    @JvmStatic
    fun main(args: Array<String>) {
        val spider = ncSpider("Zhu19", "1RVAC6")
        println(spider.login())
        val classInfo = spider.classes
        for ((key,value) in classInfo) {
            println(key + ": "+ spider.getClassesGrades(value))
        }
    }
}
