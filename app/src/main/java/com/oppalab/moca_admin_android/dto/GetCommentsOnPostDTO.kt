import com.oppalab.moca_admin_android.dto.CommentsOnPost

data class GetCommentsOnPostDTO (
    val page: Int,
    val content: List<CommentsOnPost>,
)