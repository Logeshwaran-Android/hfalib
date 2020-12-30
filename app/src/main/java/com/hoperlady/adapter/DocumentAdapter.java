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

import com.hoperlady.Pojo.DocumentPojo;

import com.hoperlady.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import core.service.ServiceConstant;


public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.MyViewHolder> {

    private final AppCompatActivity aContextItem;
    private ArrayList<DocumentPojo> aDataItem;
    private LayoutInflater aInflater;
    private String myFileTypeStr = "pdf";
    private String myFileTypeStrimage = "image";
    private PickImages aPickImages;
    private AddImage addImages;
    DocumentRemove removedocument;

    String SelectDate = "";
    private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    public DocumentAdapter(ArrayList<DocumentPojo> items, AppCompatActivity context, PickImages pickImages,DocumentRemove removedocument,
                           AddImage addImages) {

        this.aDataItem = items;
        this.aContextItem = context;
        this.aPickImages = pickImages;
        this.removedocument=removedocument;
        this.addImages=addImages;

    }

    public interface PickImages {

        void Update(File captured_image, int position, String type);

        void DisplayImageInPopUp(String URL);
    }

    public interface DocumentRemove {

        void Remove(int position);

    }

    public interface AddImage {

        void Update(File captured_image, int position, String type);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.register_licence_upload_edit, parent, false);
        return new MyViewHolder(v);
    }

    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        aInflater = LayoutInflater.from(aContextItem);
        if(position==0){
            holder.licenceTitleNameTV.setText("Voter ID");
        }
         if(position==1){
            holder.licenceTitleNameTV.setText("Passport");
        }
       if(position==2){
            holder.licenceTitleNameTV.setText("Certificate");
        }
      //  holder.licenceTitleNameTV.setText(aDataItem.get(position).getName());
        if (aDataItem.get(position).getExpiryDate().equalsIgnoreCase("")) {

        } else {
            holder.expirationDateTV.setText(aDataItem.get(position).getExpiryDate());
        }
        if (!aDataItem.get(position).getImageWebUrl().isEmpty()) {
            if (aDataItem.get(position).getImageWebUrl().endsWith(".doc") || aDataItem.get(position).getImageWebUrl().endsWith(".docx")) {
                Picasso.with(aContextItem).load(R.drawable.document_files_icons).error(R.drawable.document_files_icons).into(holder.choosedImageIV);
            } else if (aDataItem.get(position).getImageWebUrl().endsWith(".pdf")) {
                Picasso.with(aContextItem).load(R.drawable.pdf_files_icons).error(R.drawable.pdf_files_icons).into(holder.choosedImageIV);
            } else {
                Picasso.with(aContextItem).load(ServiceConstant.Review_image + aDataItem.get(position).getImageWebUrl()).error(R.drawable.pdf_icon).fit().into(holder.choosedImageIV);
            }

            holder.choosedImageIV.setVisibility(View.VISIBLE);

            holder.document_view_tv.setVisibility(View.VISIBLE);
            holder.document_update_tv.setVisibility(View.VISIBLE);
            holder.document_delete_tv.setVisibility(View.VISIBLE);

            holder.add_document_tv.setVisibility(View.GONE);

        } else{

            holder.add_document_tv.setVisibility(View.VISIBLE);

            holder.document_view_tv.setVisibility(View.GONE);
            holder.document_update_tv.setVisibility(View.GONE);
            holder.document_delete_tv.setVisibility(View.GONE);

            holder.choosedImageIV.setVisibility(View.GONE);
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

        holder.licenceUploadMessageTV.setText(aContextItem.getString(R.string.license_adapter_upload_your_text));
        holder.EditImage.setOnClickListener(new View.OnClickListener() {
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
                    viewPicTV.setVisibility(View.GONE);
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

        holder.document_view_tv.setOnClickListener(new View.OnClickListener() {
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

        holder.document_update_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                    viewPicTV.setVisibility(View.GONE);
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

        holder.document_delete_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removedocument.Remove(position);
            }
        });

        holder.add_document_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                    viewPicTV.setVisibility(View.GONE);
                } else {
                    viewPicTV.setVisibility(View.GONE);
                }


                cameraPicTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        addImages.Update(captured_image, position, "camera");

                    }
                });
                galleryPicTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        addImages.Update(captured_image, position, "gallery");
                    }
                });
                documentPicTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        addImages.Update(captured_image, position, "pdf");
                    }
                });
                docPicTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        addImages.Update(captured_image, position, "document");

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
        public ImageView choosedImageIV, EditImage;
        public TextView licenceUploadMessageTV;
        public TextView licenceTitleNameTV;
        public TextView expirationDateTV;
        public TextView document_view_tv;
        public TextView document_update_tv;
        public TextView document_delete_tv;
        public TextView add_document_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            uploadImageRL = (RelativeLayout) itemView.findViewById(R.id.uploadImageRL);

            uploadBottumSheetRL = (RelativeLayout) itemView.findViewById(R.id.uploadBottumSheetRL);
            datePickerRL = (RelativeLayout) itemView.findViewById(R.id.datePickerRL);
            choosedImageIV = (ImageView) itemView.findViewById(R.id.choosedImageIV);
            EditImage = (ImageView) itemView.findViewById(R.id.EditImage);
            licenceUploadMessageTV = (TextView) itemView.findViewById(R.id.licenceUploadMessageTV);
            licenceTitleNameTV = (TextView) itemView.findViewById(R.id.licenceTitleNameTV);
            expirationDateTV = (TextView) itemView.findViewById(R.id.expirationDateTV);

            document_view_tv=(TextView)itemView.findViewById(R.id.document_view_tv);
            document_update_tv=(TextView)itemView.findViewById(R.id.document_update_tv);
            document_delete_tv=(TextView)itemView.findViewById(R.id.document_delete_tv);

            add_document_tv=(TextView)itemView.findViewById(R.id.add_document_tv);
        }
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
}