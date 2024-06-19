package raj.animesh.image_uploader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<DataClass> arrayList;
    private Context context;

    public MyAdapter(ArrayList<DataClass> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.recycler_item,
                parent,
                false
        );
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DataClass dataClass = arrayList.get(position);
        holder.captionTxt.setText(dataClass.getCaption());

        Glide.with(context).load(dataClass.imageUrl).into(holder.captionImage);

        holder.captionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make a dialog box that will show the image in proper size/ full size
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView captionTxt;
        ImageView captionImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            captionImage = itemView.findViewById(R.id.recyclerImage);
            captionTxt = itemView.findViewById(R.id.recyclerCaption);

        }
    }
}
