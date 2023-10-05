import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingapp.R
import com.example.bookingapp.models.Trip

class StationAdapter(
    private val istasyonList: List<Trip>?,
    private val onItemClickListener: (Trip) -> Unit
) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_station, parent, false)
        return StationViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val istasyon = istasyonList?.get(position)
        holder.bind(istasyon)
    }

    override fun getItemCount(): Int {
        return istasyonList?.size ?: 0
    }

    inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textStationName: TextView = itemView.findViewById(R.id.textStationName)
        private val bookButton: Button = itemView.findViewById(R.id.bookButton)

        init {
            bookButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedTrip = istasyonList?.get(position)
                    selectedTrip?.let { onItemClickListener(it) }
                }
            }
        }

        fun bind(istasyon: Trip?) {
            if (istasyon != null) {
                textStationName.text = istasyon.bus_name
            }
        }
    }
}