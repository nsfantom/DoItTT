package tm.fantom.doittesttask.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import tm.fantom.doittesttask.R;
import tm.fantom.doittesttask.api.model.Image;

/**
 * Created by fantom on 02-Oct-17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> implements Consumer<List<Image>> {


    private List<Image> imageList = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position != holder.getAdapterPosition()) return;
        Image image = imageList.get(position);
        holder.tvWeather.setText(image.getImageParametersList().getWeather());
        holder.tvAddress.setText(image.getImageParametersList().getAddress());
        Glide.with(holder.view.getContext()).load(image.getSmallImagePath())
                //.crossFade()
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public void accept(List<Image> imageList) throws Exception {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvWeather) TextView tvWeather;
        @BindView(R.id.tvAddress) TextView tvAddress;
        @BindView(R.id.imageView) ImageView imageView;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this,itemView);
        }
    }
}
