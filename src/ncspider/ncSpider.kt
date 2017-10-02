package ncspider

import java.io.IOException
import org.jsoup.Connection.Method
import org.jsoup.Connection.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class ncSpider(private var username: String, private var password: String)// TODO Auto-generated constructor stub
{
    var NC_BASE_URL = "http://netclassroom.chaminade.org"
    var NC_LOGIN_URL = NC_BASE_URL + "/NetClassroom7/Forms/login.aspx"
    var NC_SHELL_URL = "http://netclassroom.chaminade.org/NetClassroom7/Forms/NCShell.aspx"
    lateinit var req: Response
    var cookies = mutableMapOf<String, String>()

    var classes = mutableMapOf<String, String>()
        get() {
            navigate2Page("myMenuId\$Menu1", "mnuPerformance")
            var mClasses = mutableMapOf<String, String>()
            val doc = this.req.parse()
            val classSpinner = doc.getElementById("_ctl14_cpWhatever_lstWhatever").getElementsByTag("option")
            for (e:Element in classSpinner) {
                mClasses.put((e.text().split(",")[1].trim()), e.`val`())
            }
            return mClasses
        }

    var getSchedule = mutableListOf<Map<String, String>>()
        get() {
            login()
            navigate2Page("myMenuId:Menu1","mnuScheduleCalendar");
            val mSchedule = mutableListOf<Map<String, String>>()
            val content = this.req.parse().body().getElementById("Table1").getElementsByTag("span")
            for(date:Element in content) {
                val info =  date.text().split(",")
                if (info.size == 5) {
                    val schedule = mutableMapOf<String, String>()
                    schedule.put("block", info[0])
                    schedule.put("time", info[1])
                    schedule.put("class", info[2])
                    schedule.put("teacher", info[3])
                    schedule.put("room", info[4])
                    mSchedule.add(schedule)
                }
            }
            return (mSchedule)
        }

    private fun navigate2Page(eventTarget:String, eventArgument:String, fakeInputs:MutableMap<String,String> = mutableMapOf()){
        var inputs = mutableMapOf<String,String>()
        val content = this.req.parse().body().getElementById("Form1").getElementsByTag("input")
        for (e in content) {
            if (e.id() !== "") {
                inputs.put(e.id(), e.`val`());
            }
        }
        for ((key,value) in fakeInputs.entries) {
            inputs.put(key, value)
        }
        inputs.put("__postbackAction", "dont_save")
        inputs.put("__EVENTTARGET", eventTarget)
        inputs.put("__EVENTARGUMENT", eventArgument)
        inputs.put("availableWidth", "800")
        inputs.put("availableHeight", "600")
        this.req = Jsoup.connect(NC_SHELL_URL)
                .userAgent("NCSpyder/1.0")
                .referrer(NC_LOGIN_URL)
                .cookies(this.cookies)
                .data(inputs)
                .method(Method.POST)
                .execute()
    }

    @Throws(RuntimeException::class)
    fun login(): Boolean {
        val inputs = mutableMapOf<String,String>()
        this.req = Jsoup.connect(NC_LOGIN_URL)
                .userAgent("NCSpyder/1.0")
                .referrer(NC_LOGIN_URL)
                .timeout(5000)
                .method(Method.GET)
                .execute()
        this.cookies = this.req.cookies()
        val content = req.parse().body().getElementsByTag("input")
        for (e in content) {
            inputs.put(e.id(), "")
        }
        inputs.put("sid", this.username)
        inputs.put("pin", this.password)
        this.req = Jsoup.connect(NC_LOGIN_URL)
                .userAgent("NCSpyder/1.0")
                .referrer(NC_LOGIN_URL)
                .cookies(this.cookies)
                .data(inputs)
                .method(Method.POST)
                .execute()
        try {
            if (this.req.parse().text().toLowerCase().indexOf("loading") >= 0) {
                println("login successful!")
                this.cookies = this.req.cookies()
                return true
            } else {
                throw RuntimeException("Login failed for user " + this.username)
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return false
    }

    fun getClassesGrades(courseId: String):String {
        val classInfo = mutableMapOf("_ctl14:cpWhatever:lstWhatever" to courseId)
        navigate2Page("_ctl14\$cpWhatever\$lstWhatever", "", classInfo)
        val content = this.req.parse().body()
                .getElementById("ncContent_webDG")
                .getElementsByTag("span")[1].text().split(':')[1].trim()
        return (content)
    }
}
