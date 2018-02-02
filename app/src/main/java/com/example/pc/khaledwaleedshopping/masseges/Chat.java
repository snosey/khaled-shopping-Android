package com.example.pc.khaledwaleedshopping.masseges;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeEditText;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.image.CircleTransform;
import com.example.pc.khaledwaleedshopping.Support.image.CompressImage;
import com.example.pc.khaledwaleedshopping.Support.image.UploadImage;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.clientprofile.ClientProfile;
import com.example.pc.khaledwaleedshopping.products.productprofile.ProductProfile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.pc.khaledwaleedshopping.MainActivity.chatOn;
import static com.example.pc.khaledwaleedshopping.MainActivity.jsonObjectUser;


/**
 * Created by ahmed on 6/26/2017.
 */

public class Chat extends Fragment {
    RecyclerView recyclerViewChat;
    CustomeButton sendButton;
    CustomeEditText msgText;
    String clientId, id_product;
    ImageView productLogo;
    String logoUrl = "";
    private String userId;
    CustomeTextView title;
    private List<Uri> listImages;
    private ImageView sendImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat, container, false);

        listImages = new ArrayList<>();

        sendImg = (ImageView) view.findViewById(R.id.sendImage);
        ImageView back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        productLogo = (ImageView) view.findViewById(R.id.productImage);
        productLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProduct();
            }
        });
        chatOn = true;
        title = (CustomeTextView) view.findViewById(R.id.title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProduct();
            }
        });
        sendButton = (CustomeButton) view.findViewById(R.id.send);
        msgText = (CustomeEditText) view.findViewById(R.id.msgText);
        Bundle bundle = getArguments();
        id_product = bundle.getString("id_product");
        clientId = bundle.getString("id");

        try {
            userId = jsonObjectUser.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerViewChat = (RecyclerView) view.findViewById(R.id.chatList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewChat.setLayoutManager(layoutManager);

        getChat();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg("");
            }
        });

        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                    try {
                        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1);
                        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    private void sendMsg(String kind) {
        {
            if (!kind.equals("") || !msgText.getText().toString().equals("")) {
                UrlData urlData = new UrlData();
                if (!msgText.getText().toString().equals(""))
                    kind = msgText.getText().toString();
                urlData.add("message", kind);
                urlData.add("id_recieve", clientId);
                urlData.add("id_sent", userId);
                urlData.add("id_product", id_product);


                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String result) {

                        if (result != null) {
                            msgText.setText("");
                            getChat();
                        }

                    }
                }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.sendMsg, urlData.get());

            } else
                Toast.makeText(getActivity(), "You cant send empty message!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getChat() {
        UrlData urlData = new UrlData();
        urlData.add("id_recieve", clientId);
        urlData.add("id_sent", userId);
        urlData.add("id_product", id_product);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                if (result != null) {
                    recyclerViewChat.setAdapter(new chatAdapter(result));
                }

            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.getChat, urlData.get());

    }

    private class chatAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private JSONArray chatList;

        private chatAdapter(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                logoUrl = jsonObject.getString("reciver_logo");
                title.setText(jsonObject.getString("product_name"));

                if (!jsonObject.getString("product_image").equals(""))
                    Picasso.with(getContext()).load(WebService.imageLink + jsonObject.getString("product_image")).transform(new CircleTransform()).into(productLogo);

                chatList = jsonObject.getJSONArray("data");
                recyclerViewChat.scrollToPosition(chatList.length() - 1);
                int i = chatList.length() - 1;
                UrlData urlData = new UrlData();
                for (; i >= 0; i--) {
                    if (chatList.getJSONObject(i).getString("id_sent").equals(clientId)) {
                        if (chatList.getJSONObject(i).getString("seen").equals("1"))
                            break;
                        else {
                            urlData.add("id[]", chatList.getJSONObject(i).getString("id"));
                        }
                    }
                }
                if (i != chatList.length() - 1) {
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String result) {

                            UrlData urlData = new UrlData();
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", MODE_PRIVATE);

                            urlData.add("username", sharedPreferences.getString("username", ""));
                            urlData.add("password", sharedPreferences.getString("password", ""));

                            new GetData(new GetData.AsyncResponse() {
                                @Override
                                public void processFinish(String output) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(output);
                                        if (jsonObject.getString("sucess").equals("true")) {
                                            jsonObjectUser = jsonObject;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.login, urlData.get());


                        }
                    }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.seeMsg, urlData.get());

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {

                final JSONObject chatRow = chatList.getJSONObject(position);

                if (!chatRow.getString("id_sent").equals(userId)) {

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.RIGHT_OF, R.id.logo);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    holder.msg.setLayoutParams(params);
                    holder.msg.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    holder.msg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.blue)));
                    holder.date.setVisibility(View.VISIBLE);
                    holder.logo.setVisibility(View.VISIBLE);
                    holder.logo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("id", clientId);
                            ClientProfile clientProfile = new ClientProfile();
                            clientProfile.setArguments(bundle);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.addToBackStack(null);
                            ft.replace(R.id.activity_main_content_fragment3, clientProfile);
                            ft.commit();
                        }
                    });
                    if (!logoUrl.equals(""))
                        Picasso.with(getActivity()).load(WebService.imageLink + logoUrl).transform(new CircleTransform()).into(holder.logo, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                holder.logo.setImageResource(R.drawable.profile);
                            }
                        });

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.HOUR, -6);
                    try {

                        Date currentDate = format.parse(format.format(c.getTime()));
                        Date created_at = format.parse(chatRow.getString("created_at").substring(0, 10));
                        int days = (int) ((currentDate.getTime() - created_at.getTime()) / (1000 * 60 * 60 * 24));
                        if (days == 0)
                            holder.date.setText("today");
                        else if (days == 1)
                            holder.date.setText("yesterday");
                        else holder.date.setText(days + " days");

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    holder.date.setVisibility(View.GONE);
                    holder.logo.setVisibility(View.GONE);
                    holder.msg.setTextColor(ContextCompat.getColor(getContext(), R.color.black_8am2));

                    if (chatRow.getString("seen").equals("1"))
                        holder.msg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
                    else
                        holder.msg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.whiteDark)));

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    holder.msg.setLayoutParams(params);
                }

                if (chatRow.getString("message").endsWith(".jpg") || chatRow.getString("message").endsWith(".png") || chatRow.getString("message").endsWith(".jpeg") ||
                        chatRow.getString("message").endsWith(".JPG") || chatRow.getString("message").endsWith(".PNG") || chatRow.getString("message").endsWith(".JPEG")) {

                    {
                        Picasso.with(getContext()).load(WebService.imageLink + chatRow.getString("message")).into(holder.img, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.msg.setVisibility(View.GONE);
                                holder.img.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                holder.img.setVisibility(View.GONE);
                            }
                        });
                        holder.img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    new FullScreen(getActivity(), chatRow.getString("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    holder.msg.setVisibility(View.VISIBLE);
                    holder.img.setVisibility(View.GONE);
                    holder.msg.setText(chatRow.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            try {
                return (int) chatList.length();
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomeTextView date, msg;
        public ImageView logo;
        public ImageView img;

        public MyViewHolder(View v) {
            super(v);

            date = (CustomeTextView) v.findViewById(R.id.date);
            msg = (CustomeTextView) v.findViewById(R.id.msg);
            logo = (ImageView) v.findViewById(R.id.logo);
            img = (ImageView) v.findViewById(R.id.img);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        chatOn = false;
    }

    void openProduct() {
        Bundle bundle = new Bundle();
        bundle.putString("id", id_product);
        ProductProfile productprofile = new ProductProfile();
        productprofile.setArguments(bundle);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.activity_main_content_fragment3, productprofile);
        ft.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            listImages.clear();
            CompressImage compressImage = new CompressImage();
            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(compressImage.compress(new File(images.get(i).path)));
                listImages.add(i, uri);
            }
            new UploadImage(new UploadImage.AsyncResponse() {
                @Override
                public void processFinish(boolean done) {
                    if (done) {
                        for (int i = 0; i < listImages.size(); i++) {
                            sendMsg(getFileName(listImages.get(i)));
                        }
                    }
                }
            }, getActivity(), listImages);

        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDateandTime = sdf.format(new Date());

        if (!(result.endsWith("jpg") || result.endsWith("png") || result.endsWith("jpeg"))) {
            result += ".png";
        }

        return currentDateandTime + result;
    }


}

