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
        for (course in spider.getSchedule) {
            println(course["block"] + course["time"] + course["class"] + course["teacher"] + course["room"])
        }
    }
}
