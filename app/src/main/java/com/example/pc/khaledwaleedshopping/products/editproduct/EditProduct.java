package com.example.pc.khaledwaleedshopping.products.editproduct;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeDialogMassege;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeEditText;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.image.CompressImage;
import com.example.pc.khaledwaleedshopping.Support.image.UploadImage;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.products.home.Home;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;

import static android.app.Activity.RESULT_OK;
import static com.example.pc.khaledwaleedshopping.MainActivity.fragmentHome;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by pc on 10/5/2017.
 */

public class EditProduct extends Fragment {
    RecyclerView productImages;
    CustomeTextView priceError, titleError, imageError;
    static CustomeTextView location;
    ImageAdapter imageAdapter;
    String views;
    List<Uri> imageList;
    Spinner colors1, colors2, brands, condition;
    protected static Spinner size;
    CheckBox swap;
    ImageView close;
    CustomeButton update, delete;
    String id;
    CustomeButton category;
    CustomeEditText title, description, price;
    protected static JSONObject jsonObject = null;
    protected static int categoryNumber;
    protected static String category1_id, category2_id, category3_id, city_id, government_id;
    protected static CustomeTextView categoryTitle, textSize;
    protected static String category2Title = "", category1Title = "", govTitle = "";
    private String productId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_product, container, false);
        Bundle bundle = getArguments();
        textSize = (CustomeTextView) view.findViewById(R.id.text_size);
        productId = bundle.getString("id");
        try {
            id = MainActivity.jsonObjectUser.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        categoryNumber = 0;
        category1_id = "-1";
        city_id = "-1";
        government_id = "-1";
        category2_id = "11";
        category3_id = "2";

        priceError = (CustomeTextView) view.findViewById(R.id.priceError);
        titleError = (CustomeTextView) view.findViewById(R.id.titleError);
        imageError = (CustomeTextView) view.findViewById(R.id.imageError);
        swap = (CheckBox) view.findViewById(R.id.swap);
        location = (CustomeTextView) view.findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (government_id.equals("-1"))
                    new Location(getActivity(), "gov", "-1");
                else
                    new Location(getActivity(), "city", government_id);

            }
        });
        price = (CustomeEditText) view.findViewById(R.id.price);
        price.addTextChangedListener(new TextWatcher() {
            int len = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = price.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = price.getText().toString();
                if (str.length() == 1 && len == 0) {
                    price.setText("L.E~" + str);
                    Selection.setSelection(price.getText(), price.getText().length());
                } else if (str.length() == 1 && len == 2) price.setText("");

            }
        });
        title = (CustomeEditText) view.findViewById(R.id.title);
        description = (CustomeEditText) view.findViewById(R.id.description);
        categoryTitle = (CustomeTextView) view.findViewById(R.id.category_title);
        category = (CustomeButton) view.findViewById(R.id.category);
        update = (CustomeButton) view.findViewById(R.id.edit);
        delete = (CustomeButton) view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext());
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                new GetData(new GetData.AsyncResponse() {
                                    @Override
                                    public void processFinish(String result) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
                                            if (jsonObject.getString("sucess").equals("true")) {

                                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                                FragmentTransaction ft = fm.beginTransaction();
                                                fragmentHome = new Home();
                                                Home.downScroll = -1;
                                                ft.replace(R.id.activity_main_content_fragment3, fragmentHome);
                                                ft.commit();
                                                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.deleteProduct, productId);


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
        close = (ImageView) view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext());
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().onBackPressed();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        colors1 = (Spinner) view.findViewById(R.id.colors1);
        colors2 = (Spinner) view.findViewById(R.id.colors2);
        brands = (Spinner) view.findViewById(R.id.brands);
        size = (Spinner) view.findViewById(R.id.size);
        condition = (Spinner) view.findViewById(R.id.condition);
        swap = (CheckBox) view.findViewById(R.id.swap);

        imageList = new ArrayList<>();
        Uri path = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.drawable.add_image);
        imageList.add(path);
        imageAdapter = new ImageAdapter();
        productImages = (RecyclerView) view.findViewById(R.id.productImages);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        productImages.setLayoutManager(layoutManager);
        productImages.setAdapter(imageAdapter);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                try {
                    jsonObject = new JSONObject(result);
                    colors1.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("color"), "color", "color1"));
                    colors2.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("color"), "color", "color2"));
                    brands.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("brands"), "name", "brands"));
                    condition.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("condition"), "title", "condition"));
                    size.setVisibility(View.GONE);
                    textSize.setVisibility(View.GONE);
                    category.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Category(getActivity());
                        }
                    });
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateProduct();
                        }
                    });

                    setData();
                    //  Log.e("jArray", jsonObject.get);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.addProductData, "");
        return view;
    }

    private void setData() {

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    category2Title = jsonObject.getString("category2");
                    category1Title = jsonObject.getString("category1");
                    govTitle = jsonObject.getString("government");

                    views = jsonObject.getString("view");

                    String categoryText = category1Title;
                    categoryNumber = 0;
                    if (!category2Title.equals("")) {
                        categoryText = category2Title;
                        categoryNumber = 1;
                    }
                    if (!jsonObject.getString("category3").equals("")) {
                        categoryText = jsonObject.getString("category3");
                        categoryNumber = 2;
                    }
                    categoryTitle.setText(categoryText);

                    category1_id = jsonObject.getString("id_category1");
                    category2_id = jsonObject.getString("id_category2");
                    category3_id = jsonObject.getString("id_category3");
                    city_id = jsonObject.getString("id_city");
                    government_id = jsonObject.getString("id_government");


                    title.setText(jsonObject.getString("title"));
                    description.setText(jsonObject.getString("description"));
                    price.setText("L.E~" + jsonObject.getString("price"));

                    if (jsonObject.getString("swap").equals("0"))
                        swap.setChecked(false);
                    else
                        swap.setChecked(true);

                    location.setText(jsonObject.getString("city"));


                    if (!jsonObject.getString("img1").equals(" "))
                        imageList.add(Uri.parse(WebService.imageLink + jsonObject.getString("img1")));

                    if (!jsonObject.getString("img2").equals(" "))
                        imageList.add(Uri.parse(WebService.imageLink + jsonObject.getString("img2")));

                    if (!jsonObject.getString("img3").equals(" "))
                        imageList.add(Uri.parse(WebService.imageLink + jsonObject.getString("img3")));

                    if (!jsonObject.getString("img4").equals(" "))
                        imageList.add(Uri.parse(WebService.imageLink + jsonObject.getString("img4")));

                    if (!jsonObject.getString("img5").equals(" "))
                        imageList.add(Uri.parse(WebService.imageLink + jsonObject.getString("img5")));

                    imageAdapter.notifyDataSetChanged();

                    colors1.setSelection(CustomeAdapter.color1map.get(jsonObject.getString("id_color1")));
                    colors2.setSelection(CustomeAdapter.color2map.get(jsonObject.getString("id_color2")));
                    brands.setSelection(CustomeAdapter.brandsMap.get(jsonObject.getString("id_brand")));
                    size.setSelection(CustomeAdapter.sizeMap.get(jsonObject.getString("id_size")));
                    condition.setSelection(CustomeAdapter.condtionMap.get(jsonObject.getString("id_condition_state")));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.editProductData, productId);


    }

    private void updateProduct() {
        {

            if (dataComplete())
                new UploadImage(new UploadImage.AsyncResponse() {
                    @Override
                    public void processFinish(boolean output) {
                        if (output) {

                            UrlData urlData = new UrlData();
                            urlData.add("id", productId);
                            urlData.add("view", views);
                            urlData.add("id_client", id);
                            urlData.add("title", title.getText().toString());
                            urlData.add("description", description.getText().toString() + " ");
                            urlData.add("id_category1", category1_id);
                            urlData.add("id_category2", category2_id);
                            urlData.add("id_category3", category3_id);
                            urlData.add("price", price.getText().toString().replace("L.E~", ""));

                            if (swap.isChecked())
                                urlData.add("swap", "1");
                            else
                                urlData.add("swap", "0");


                            for (int i = 1; i < imageList.size(); i++)
                                urlData.add("img" + i, getFileName(imageList.get(i)));
                            for (int i = imageList.size(); i < 6; i++) {
                                urlData.add("img" + i, " ");
                            }

                            CustomeTextView brandsChooseText = (CustomeTextView) brands.getSelectedView();
                            urlData.add("id_brand", brandsChooseText.getTag().toString());

                            CustomeTextView sizeChooseText = (CustomeTextView) size.getSelectedView();
                            if (size.getVisibility() == View.GONE) {
                                urlData.add("id_size", "36");
                            } else {
                                urlData.add("id_size", sizeChooseText.getTag().toString());
                            }

                            CustomeTextView conditionChooseText = (CustomeTextView) condition.getSelectedView();
                            urlData.add("id_condition_state", conditionChooseText.getTag().toString());

                            CustomeTextView colorsChooseText = (CustomeTextView) colors1.getSelectedView();
                            CustomeTextView colorsChooseText2 = (CustomeTextView) colors2.getSelectedView();
                            urlData.add("id_color1", colorsChooseText.getTag().toString());
                            urlData.add("id_color2", colorsChooseText2.getTag().toString());

                            urlData.add("id_government", government_id);
                            urlData.add("id_city", city_id);

                            new GetData(new GetData.AsyncResponse() {
                                @Override
                                public void processFinish(String result) {

                                    try {
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        fragmentHome = new Home();
                                        Home.downScroll = -1;
                                        ft.replace(R.id.activity_main_content_fragment3, fragmentHome);
                                        ft.commit();
                                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.updateProduct, urlData.get());

                        }
                    }
                }, getActivity(), imageList);
        }
    }

    private boolean dataComplete() {
        boolean b = true;
        priceError.setVisibility(View.GONE);
        titleError.setVisibility(View.GONE);
        imageError.setVisibility(View.GONE);
        if (imageList.size() == 1) {
            b = false;
            imageError.setVisibility(View.VISIBLE);
        }
        if (title.getText().length() == 0) {
            b = false;
            titleError.setVisibility(View.VISIBLE);
        }
        if (price.getText().length() == 0) {
            b = false;
            priceError.setVisibility(View.VISIBLE);
        }

        if (categoryTitle.getText().toString().equals("You didn't choose category yet.")) {
            b = false;
        }

        if (government_id.equals("-1")) {
            b = false;
        }
        return b;
    }

    private class ImageAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_product_image_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            if (position == 0) {
                holder.delete.setVisibility(View.GONE);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (imageList.size() == 6)
                            new CustomeDialogMassege(getActivity(), "You cant add more than 5 images");
                        else {
                            Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                            try {
                                intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 6 - imageList.size()); // set limit for image selection
                                startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {

                holder.delete.setVisibility(View.VISIBLE);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.image);
                        ImageView imageView = (ImageView) dialog.findViewById(R.id.image);
                        Picasso.with(getContext()).load(imageList.get(position)).into(imageView);
                        dialog.show();
                    }
                });
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageList.remove(position);
                        imageAdapter.notifyDataSetChanged();
                    }
                });
            }
            Picasso.with(getActivity()).load(imageList.get(position)).resize(400, 400).centerInside().into(holder.image);

        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image, delete;

        public MyViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            delete = (ImageView) v.findViewById(R.id.delete);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<in.myinnos.awesomeimagepicker.models.Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            CompressImage compressImage=new CompressImage();
            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(compressImage.compress(new File(images.get(i).path)));
                imageList.add(uri);
            }
            imageAdapter.notifyDataSetChanged();
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.toString().contains(WebService.imageLink))
            return uri.toString().replace(WebService.imageLink, "");
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

