import com.clarice.burrow.ui.model.journal.Journal
import com.google.gson.annotations.SerializedName

data class JournalsResponse(
    @SerializedName("data")
    val data: List<Journal>?
)
