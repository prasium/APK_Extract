package com.prasium.apkextract;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import static com.prasium.apkextract.R.drawable.invert;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> implements Filterable {

    private ArrayList<RVModel> rvModels;
    private ArrayList<RVModel> rvModelsall;
    public RVAdapter(ArrayList<RVModel> rvModel)
    {
        rvModels = rvModel;
        rvModelsall= new ArrayList<>(rvModel);
    }



    @Override
    public int getItemCount() {
        return rvModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;// prevented overlapping of list views
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    Filter filter = new Filter() {
        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<RVModel> filteredList = new ArrayList<RVModel>();
            if(charSequence.toString().isEmpty()){
                filteredList.addAll(rvModelsall);
            }else
            {
                for(RVModel r : rvModelsall){
                    if(r.getAppName().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filteredList.add(r);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values=filteredList;
            return filterResults;
        }
        //runs on a UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            rvModels.clear();
            rvModels.addAll((Collection<? extends RVModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView appNameView,packNameView;
        private ImageView appIcon;
        private Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameView=itemView.findViewById(R.id.appname);
            packNameView=itemView.findViewById(R.id.packagename);
            appIcon=itemView.findViewById(R.id.iconid);
            button=itemView.findViewById(R.id.extractbutton);
            switch(itemView.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
            {
                case Configuration.UI_MODE_NIGHT_YES:
                    button.setBackgroundResource(R.drawable.invert);
                    appNameView.setTextColor(Color.WHITE);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    break;
            }
        }
    }
   @NonNull
    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater= LayoutInflater.from(context);

        View appView = inflater.inflate(R.layout.applist,parent,false);
        return new ViewHolder(appView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final RVModel model = rvModels.get(position);
        // Set item views based on your views and data model
        holder.appNameView.setText(model.getAppName());
        holder.packNameView.setText(model.getPackageName());
        holder.appIcon.setImageDrawable(model.getAppIcon());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm=view.getContext().getPackageManager();
                ApplicationInfo ai = null;
                try {
                    ai = pm.getApplicationInfo(model.getPackageName(), 0);
                    File file = new File(ai.publicSourceDir);
                    File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extract/" + model.getAppName() + ".apk");
                    try {
                        if(!dest.getParentFile().exists())
                        dest.getParentFile().mkdirs();


                        copyFile(file, dest,view);

                        Toast.makeText(view.getContext(),"Extracted to "+dest.getParentFile(),Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e( "extractpackage", "Exception, Message: " + e.getMessage() );
                        new AlertDialog.Builder(view.getContext() , R.style.AppCompatAlertDialogError )
                                .setTitle( "Exception detected" )
                                .setMessage( "Exception detected: " + e.getMessage() )
                                .setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick( DialogInterface dialog, int which ) {

                                    }
                                }).show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }


        public static void copyFile(File src, File dst,View v) throws IOException {

            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            // Transfer bytes from in to out

          byte[] buf = new byte[1024];
            int len;
            long totalBytesCopied=0;
            long expectedBytes = src.length();
         while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                totalBytesCopied+=len;
                int progress = (int) Math.round(((double) totalBytesCopied / (double) expectedBytes) * 100);
            }
            in.close();
            out.close();
        }



}
