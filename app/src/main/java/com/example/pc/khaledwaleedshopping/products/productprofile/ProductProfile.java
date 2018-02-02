package com.example.pc.khaledwaleedshopping.products.productprofile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomSliderView;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.image.CircleTransform;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.clientprofile.ClientProfile;
import com.example.pc.khaledwaleedshopping.masseges.Chat;
import com.example.pc.khaledwaleedshopping.products.editproduct.EditProduct;
import com.example.pc.khaledwaleedshopping.products.home.ProductListAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pc on 10/15/2017.
 */

public class ProductProfile extends Fragment {

    ////////////// TopBar
    ImageView back;
    CustomeTextView productTitle1;

    ///////////////
    SliderLayout productImages;

    ////////////// Client info
    ImageView logo;
    CustomeTextView clientName, clientReviews;

    ///////////// Product info
    CustomeTextView productTitle2, sizeAndState, price;
    CustomeButton messageSeller, editItem;
    ImageView call, share, favourite;

    //////////// Product Description
    CustomeTextView itemDescription, color, peopleIntersted, added, viewCount, location;

    /////////// Comments
    CustomeTextView comments;

    /////////// Similar Products
    CustomeTextView memberItems, similarItems;


    /////////// others
    String productId;
    RelativeLayout client;
    RecyclerView productRV;
    JSONArray clientProducts, similarProducts;
    ProductListAdapter productListAdapter;
    String whichButton = "client";
    private int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_profile, container, false);

        back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        Bundle bundle = getArguments();
        productId = bundle.getString("id");

        setProductInfo(view);

        productRV = (RecyclerView) view.findViewById(R.id.productList);
        final GridLayoutManager mLayoutManager;
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        mLayoutManager = new GridLayoutManager(getActivity(), noOfColumns);
        productRV.setLayoutManager(mLayoutManager);
        clientProducts = new JSONArray();
        similarProducts = new JSONArray();
        productListAdapter = new ProductListAdapter(clientProducts, getActivity(), "profile_main");
        productRV.setAdapter(productListAdapter);


        memberItems = (CustomeTextView) view.findViewById(R.id.memberItems);
        similarItems = (CustomeTextView) view.findViewById(R.id.similarItems);

        memberItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (whichButton.equals("brand")) {
                    similarItems.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.darker_gray, null));
                    memberItems.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black_8am2, null));
                    whichButton = "client";
                    productListAdapter = new ProductListAdapter(clientProducts, getActivity(), "profile_main");
                    productRV.setAdapter(productListAdapter);
                }
            }
        });

        similarItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (whichButton.equals("client")) {

                    memberItems.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.darker_gray, null));
                    similarItems.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black_8am2, null));
                    whichButton = "brand";
                    productListAdapter = new ProductListAdapter(similarProducts, getActivity(), "Inbox");
                    productRV.setAdapter(productListAdapter);
                }
            }
        });

        updateView(productId);
        return view;
    }

    private void updateView(String productId) {
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.updateView + productId, "");

    }

    private void loadingProducts(String id_category1, String id_category2, String id_category3, String clientId) {

        UrlData urlData = new UrlData();

        urlData.add("id", clientId);
        try {
            urlData.add("user_id", MainActivity.jsonObjectUser.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    try {
                        clientProducts = new JSONArray(result);
                        if (clientProducts.length() != 0) {
                            productListAdapter = new ProductListAdapter(clientProducts, getActivity(), "profile_main");
                            productRV.setAdapter(productListAdapter);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.clientProducts + clientId + "&limit=0", "&user_id=" + MainActivity.jsonObjectUser.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UrlData urlData1 = new UrlData();
        urlData.add("limit", "0");
        try {
            urlData.add("user_id", MainActivity.jsonObjectUser.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urlData.add("id_category1", id_category1);
        urlData.add("id_category2", id_category2);
        urlData.add("id_category3", id_category3);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                try {
                    similarProducts = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.similarProducts, urlData.get());

    }


    private void setProductInfo(final View view) {

        //text
        productTitle1 = (CustomeTextView) view.findViewById(R.id.productTitle1);
        clientName = (CustomeTextView) view.findViewById(R.id.clientName);
        viewCount = (CustomeTextView) view.findViewById(R.id.viewCount);
        location = (CustomeTextView) view.findViewById(R.id.location);
        clientReviews = (CustomeTextView) view.findViewById(R.id.clientReviews);
        productTitle2 = (CustomeTextView) view.findViewById(R.id.productTitle2);
        sizeAndState = (CustomeTextView) view.findViewById(R.id.sizeAndState);
        price = (CustomeTextView) view.findViewById(R.id.price);
        itemDescription = (CustomeTextView) view.findViewById(R.id.itemDescription);
        color = (CustomeTextView) view.findViewById(R.id.color);
        peopleIntersted = (CustomeTextView) view.findViewById(R.id.peopleIntersted);
        added = (CustomeTextView) view.findViewById(R.id.added);
        comments = (CustomeTextView) view.findViewById(R.id.comments);
        messageSeller = (CustomeButton) view.findViewById(R.id.messageSeller);
        editItem = (CustomeButton) view.findViewById(R.id.edit_item);
        //logo
        logo = (ImageView) view.findViewById(R.id.logo);

        //call&share&love
        call = (ImageView) view.findViewById(R.id.call);
        share = (ImageView) view.findViewById(R.id.share);
        favourite = (ImageView) view.findViewById(R.id.favourite);

        //productImages
        productImages = (SliderLayout) view.findViewById(R.id.productImages);
        productImages.stopAutoCycle();

        try {
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    try {
                        final JSONObject productDetails = new JSONObject(result);

                        if (productDetails.getString("id_client").equals(MainActivity.jsonObjectUser.getString("id"))) {
                            messageSeller.setVisibility(View.GONE);
                            editItem.setVisibility(View.VISIBLE);
                        } else {
                            messageSeller.setVisibility(View.VISIBLE);
                            editItem.setVisibility(View.GONE);
                        }
                        editItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id", productId);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                EditProduct fragment = new EditProduct();
                                fragment.setArguments(bundle);
                                ft.replace(R.id.activity_main_content_fragment3, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });
                        messageSeller.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                try {
                                    bundle.putString("id", productDetails.getString("id_client"));
                                    bundle.putString("id_product", productId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                Chat chat = new Chat();
                                chat.setArguments(bundle);
                                ft.addToBackStack(null);
                                ft.replace(R.id.activity_main_content_fragment3, chat);
                                ft.commit();
                            }
                        });

                        if (productDetails.getString("isLove").equals("false")) {
                            favourite.setTag("not love");
                            favourite.setImageResource(R.drawable.love);
                        } else {
                            favourite.setTag("love");
                            favourite.setImageResource(R.drawable.lovefull);
                        }
                        favourite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UrlData urlDataLove = new UrlData();
                                try {

                                    urlDataLove.add("id_client", MainActivity.jsonObjectUser.getString("id"));
                                    urlDataLove.add("id_product", productId);
                                    if (favourite.getTag().toString().equals("love")) {
                                        favourite.setTag("not love");
                                        urlDataLove.add("state", "remove");
                                        count--;
                                        favourite.setImageResource(R.drawable.love);
                                    } else {
                                        favourite.setTag("love");
                                        urlDataLove.add("state", "add");
                                        count++;
                                        Toast.makeText(getActivity(), "Adding to your list of favourites...", Toast.LENGTH_SHORT).show();
                                        favourite.setImageResource(R.drawable.lovefull);
                                    }
                                    peopleIntersted.setText((int) ((Integer.parseInt(productDetails.getString("love")) + count)) + "");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new GetData(new GetData.AsyncResponse() {
                                    @Override
                                    public void processFinish(String result) {

                                    }
                                }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.updateLove, urlDataLove.get());

                            }
                        });


                        peopleIntersted.setText((int) ((Integer.parseInt(productDetails.getString("love")) + count)) + "");


                        share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Intent i = new Intent(Intent.ACTION_SEND);
                                    i.setType("text/plain");
                                    i.putExtra(Intent.EXTRA_SUBJECT, "Your application name");
                                    String sAux = "\nLet me recommend you this product\n\n";
                                    sAux = sAux + productDetails.getString("share") + " \n\n";
                                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                                    startActivity(Intent.createChooser(i, "choose one"));
                                } catch (Exception e) {
                                    //e.toString();
                                }
                            }
                        });
                        loadingProducts(productDetails.getString("id_category1"), productDetails.getString("id_category2"), productDetails.getString("id_category3"), productDetails.getString("id_client"));

                        productTitle1.setText(productDetails.getString("title"));
                        clientName.setText(productDetails.getString("name"));

                        if (productDetails.getString("review").equals("0"))
                            clientReviews.setText("No reviews yet");
                        else {
                            int stars = (int) Math.round(Double.parseDouble(productDetails.getString("stars")));
                            ;
                            clientReviews.setText(productDetails.getString("review"));
                            if (stars == 1)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star1, 0, R.drawable.edit_user, 0);
                            else if (stars == 2)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star2, 0, R.drawable.edit_user, 0);
                            else if (stars == 3)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star3, 0, R.drawable.edit_user, 0);
                            else if (stars == 4)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star4, 0, R.drawable.edit_user, 0);
                            else if (stars == 5)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star5, 0, R.drawable.edit_user, 0);

                        }
                        client = (RelativeLayout) view.findViewById(R.id.client);
                        client.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                try {
                                    bundle.putString("id", productDetails.getString("id_client"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ClientProfile fragment = new ClientProfile();
                                fragment.setArguments(bundle);
                                ft.replace(R.id.activity_main_content_fragment3, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            }

                        });

                        productTitle2.setText(productDetails.getString("title"));
                        sizeAndState.setText(productDetails.getString("size") + " - " + productDetails.getString("state")
                                + " - " + productDetails.getString("brand"));

                        price.setText(" L.E~" + productDetails.getString("price"));
                        itemDescription.setText(productDetails.getString("description"));
                        color.setText(productDetails.getString("color1") + " - " + productDetails.getString("color2"));
                        viewCount.setText(productDetails.getString("view"));

                        location.setText(productDetails.getString("government") + " - " + productDetails.getString("city"));
                        peopleIntersted.setText((int) ((Integer.parseInt(productDetails.getString("love")) + count)) + "");

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.HOUR, -6);
                        try {

                            Date currentDate = format.parse(format.format(c.getTime()));
                            Date created_at = format.parse(productDetails.getString("created_at").substring(0, 10));
                            int days = (int) ((currentDate.getTime() - created_at.getTime()) / (1000 * 60 * 60 * 24));
                            if (days == 0)
                                added.setText("today");
                            else if (days == 1)
                                added.setText("yesterday");
                            else added.setText(days + " days");

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        comments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("productId", productId);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ProductComments productComments = new ProductComments();
                                productComments.setArguments(bundle);
                                ft.replace(R.id.activity_main_content_fragment3, productComments);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });
                        comments.setText("comments ( " + productDetails.getString("comment") + " )");

                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                try {
                                    intent.setData(Uri.parse("tel:" + productDetails.getString("phone")));
                                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE};
                                        if (!MainActivity.hasPermissions(getActivity(), PERMISSIONS))
                                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
                                        return;
                                    }
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });


                        //images 1
                        if (!productDetails.getString("img1").equals(" ")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    try {
                                        new FullScreen(getActivity(), productDetails.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                                    .image(WebService.imageLink + productDetails.getString("img1")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            productImages.addSlider(textSliderView);
                        }
                        //images 2
                        if (!productDetails.getString("img2").equals(" ")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    try {
                                        new FullScreen(getActivity(), productDetails.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                                    .image(WebService.imageLink + productDetails.getString("img2")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            productImages.addSlider(textSliderView);
                        }
                        //images 3
                        if (!productDetails.getString("img3").equals(" ")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    try {
                                        new FullScreen(getActivity(), productDetails.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                                    .image(WebService.imageLink + productDetails.getString("img3")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            productImages.addSlider(textSliderView);
                        }
                        //images 4
                        if (!productDetails.getString("img4").equals(" ")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    try {
                                        new FullScreen(getActivity(), productDetails.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                                    .image(WebService.imageLink + productDetails.getString("img4")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            productImages.addSlider(textSliderView);
                        }
                        //images 5
                        if (!productDetails.getString("img5").equals(" ")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    try {
                                        new FullScreen(getActivity(), productDetails.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                                    .image(WebService.imageLink + productDetails.getString("img5")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            productImages.addSlider(textSliderView);
                        }


                        //logo
                        if (!productDetails.getString("logo").equals("") &&
                                !productDetails.getString("logo").contains("https:"))
                            Picasso.with(getActivity()).load(WebService.imageLink + productDetails.getString("logo")).transform(new CircleTransform()).into(logo, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    logo.setImageResource(R.drawable.profile);
                                }
                            });
                        else
                            logo.setImageResource(R.drawable.profile);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.productProfileData + "id=" + productId, "&user_id=" + MainActivity.jsonObjectUser.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
