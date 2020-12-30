package com.hoperlady.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperlady.Pojo.RegisterLicenceUploadPojo;

import com.hoperlady.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import core.service.ServiceConstant;

public class RegisterLicenceUploadAdatpter extends RecyclerView.Adapter<RegisterLicenceUploadAdatpter.MyViewHolder> {

    private final AppCompatActivity aContextItem;
    private ArrayList<RegisterLicenceUploadPojo> aDataItem;
    private LayoutInflater aInflater;
    private String myFileTypeStrimage = "image";
    private PickImages aPickImages;
    String SelectDate = "";
    private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    public RegisterLicenceUploadAdatpter(ArrayList<RegisterLicenceUploadPojo> items, AppCompatActivity context, PickImages pickImages) {
        this.aDataItem = items;
        this.aContextItem = context;
        this.aPickImages = pickImages;
    }

    public interface PickImages {
        void Update(File captured_image, int position, String type);

        void DisplayImageInPopUp(String URL);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.register_licence_upload_single, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        aInflater = LayoutInflater.from(aContextItem);
        holder.licenceTitleNameTV.setText(aDataItem.get(position).getName());
        if (aDataItem.get(position).getExpiryDate().equalsIgnoreCase("")) {

        } else {
            holder.expirationDateTV.setText(aDataItem.get(position).getExpiryDate());
        }

        if(aDataItem.get(position).getMandatory_status().equals("1")){

            holder.mandatory_icon.setVisibility(View.VISIBLE);

        } else {

            holder.mandatory_icon.setVisibility(View.VISIBLE);
        }

        if (!aDataItem.get(position).getImageWebUrl().isEmpty()) {
            if (aDataItem.get(position).getImageWebUrl().endsWith(".doc") || aDataItem.get(position).getImageWebUrl().endsWith(".docx")) {
                Picasso.with(aContextItem).load(R.drawable.document_files_icons).error(R.drawable.document_files_icons).into(holder.choosedImageIV);
            } else if (aDataItem.get(position).getImageWebUrl().endsWith(".pdf")) {
                Picasso.with(aContextItem).load(R.drawable.pdf_files_icons).error(R.drawable.pdf_files_icons).into(holder.choosedImageIV);
            } else {
                Picasso.with(aContextItem).load(ServiceConstant.Review_image + aDataItem.get(position).getImageWebUrl()).error(R.drawable.placeholder_icon).fit().into(holder.choosedImageIV);
            }
            holder.licenceUploadMessageTV.setVisibility(View.GONE);
        }

        String appDirectoryName = aContextItem.getString(R.string.app_name);
        File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appDirectoryName);
        if (!imageRoot.exists()) {
            imageRoot.mkdir();
        } else if (!imageRoot.isDirectory()) {
            imageRoot.delete();
            imageRoot.mkdir();
        }
        final String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        final File captured_image = new File(imageRoot,
                name + ".jpg");
//        if (holder.uploadImageRL!=null){
//
//
//        }
        holder.licenceUploadMessageTV.setText(aContextItem.getString(R.string.license_adapter_upload_your_text));
        // + " " + aDataItem.get(position).getName() + " " + aContextItem.getString(R.string.license_adapter_picture_text)
        holder.uploadImageRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // using BottomSheetDialog
                final View dialogView = aInflater.inflate(R.layout.bottom_sheet_image_pic_laout, null);
                final BottomSheetDialog dialog = new BottomSheetDialog(aContextItem);
                dialog.setContentView(dialogView);
                TextView cameraPicTV = (TextView) dialogView.findViewById(R.id.cameraPicTV);
                TextView galleryPicTV = (TextView) dialogView.findViewById(R.id.galleryPicTV);
                final TextView documentPicTv = (TextView) dialogView.findViewById(R.id.documentPicTV);
                final TextView docPicTv = (TextView) dialogView.findViewById(R.id.docPicTV);
                final TextView viewPicTV = (TextView) dialogView.findViewById(R.id.viewPicTV);
                final TextView cancelPicTV = (TextView) dialogView.findViewById(R.id.cancelPicTV);
                LinearLayout constraintLayout = (LinearLayout) dialogView.findViewById(R.id.bottumSheetBehavier);
                final View view = dialogView.findViewById(R.id.view);

                if (!aDataItem.get(position).getImageWebUrl().isEmpty()) {
                    viewPicTV.setVisibility(View.VISIBLE);
                } else {
                    viewPicTV.setVisibility(View.GONE);
                }


                cameraPicTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        aPickImages.Update(captured_image, position, "camera");

                    }
                });
                galleryPicTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        aPickImages.Update(captured_image, position, "gallery");
                    }
                });
                documentPicTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        aPickImages.Update(captured_image, position, "pdf");

                    }
                });
                docPicTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        aPickImages.Update(captured_image, position, "document");

                    }
                });

                viewPicTV.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {

                        if (aDataItem.get(holder.getAdapterPosition()).getImageWebUrl().endsWith("pdf")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceConstant.Review_image + aDataItem.get(holder.getAdapterPosition()).getImageWebUrl()));

                            aContextItem.startActivity(browserIntent);

                        } else if (aDataItem.get(holder.getAdapterPosition()).getImageWebUrl().endsWith("doc")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServiceConstant.Review_image + aDataItem.get(holder.getAdapterPosition()).getImageWebUrl()));
                            aContextItem.startActivity(browserIntent);


                        } else if (myFileTypeStrimage.equals("image")) {

                            aPickImages.DisplayImageInPopUp(ServiceConstant.Review_image + aDataItem.get(holder.getAdapterPosition()).getImageWebUrl());

                        }

                    }
                });


                cancelPicTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return aDataItem.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout uploadImageRL;
        private RelativeLayout uploadBottumSheetRL;
        public RelativeLayout datePickerRL;
        public ImageView choosedImageIV;
        public TextView licenceUploadMessageTV;
        public TextView licenceTitleNameTV;
        public TextView expirationDateTV;
        public ImageView mandatory_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            uploadImageRL = (RelativeLayout) itemView.findViewById(R.id.uploadImageRL);
            uploadBottumSheetRL = (RelativeLayout) itemView.findViewById(R.id.uploadBottumSheetRL);
            datePickerRL = (RelativeLayout) itemView.findViewById(R.id.datePickerRL);
            choosedImageIV = (ImageView) itemView.findViewById(R.id.choosedImageIV);
            licenceUploadMessageTV = (TextView) itemView.findViewById(R.id.licenceUploadMessageTV);
            licenceTitleNameTV = (TextView) itemView.findViewById(R.id.licenceTitleNameTV);
            expirationDateTV = (TextView) itemView.findViewById(R.id.expirationDateTV);
            mandatory_icon=(ImageView)itemView.findViewById(R.id.mandatory_icon);
        }
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
}