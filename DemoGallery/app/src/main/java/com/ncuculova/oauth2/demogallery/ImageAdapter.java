package com.ncuculova.oauth2.demogallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ncuculova.oauth2.demogallery.model.Image;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ncuculova on 6.12.15.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    List<Image> mImages;
    long mAlbumId;
    ImageViewHolder.OnImageClickListener mOnImageClickListener;
    Context mContext;

    public ImageAdapter(Context context, long albumId) {
        mContext = context;
        mAlbumId = albumId;
        this.mImages = new ArrayList<>();
    }

    public void addImage(Image image) {
        mImages.add(image);
        notifyDataSetChanged();
    }

    public void deleteImageAt(int index) {
        mImages.remove(index);
        notifyDataSetChanged();
    }

    void clear() {
        mImages.clear();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_card_view, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Image image = mImages.get(position);
        holder.mOnImageClickListener = mOnImageClickListener;
        holder.mImgId = image.id;
        holder.mAlbumId = mAlbumId;
        holder.mPosition = position;
        final String imageUrl = String.format("%s/api/image_thumb/%d", DemoGalleryHttpClient.BASE_URI, image.id);
        Picasso picasso = DemoGalleryHttpClient.getPicassoClient(mContext);
        picasso.load(imageUrl)
                .placeholder(R.drawable.ic_action_download)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public Image findImage(long imgId) {
        for (Image img : mImages) {
            if (img.id == imgId)
                return img;
        }
        return null;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image)
        ImageView mImageView;

        long mImgId;
        int mPosition;
        long mAlbumId;
        OnImageClickListener mOnImageClickListener;
        Context mContext;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(mOnImageCardClickListener);
        }

        @OnClick(R.id.btn_download)
        void downloadImage() {
            if (mOnImageClickListener != null) {
                mOnImageClickListener.onImageDownload(mImgId);
            }
        }

        @OnClick(R.id.btn_delete)
        void deleteImage() {
            if (mOnImageClickListener != null) {
                mOnImageClickListener.onImageDelete(mPosition, mAlbumId, mImgId);
            }
        }

        final View.OnClickListener mOnImageCardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener.onImageCardClick(mImgId);
                }
            }
        };

        public interface OnImageClickListener {
            void onImageDelete(final int position, long albumId, long imgId);

            void onImageDownload(long imgId);

            void onImageCardClick(long imgId);
        }
    }

    public void setOnImageClickListener(ImageViewHolder.OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }
}
