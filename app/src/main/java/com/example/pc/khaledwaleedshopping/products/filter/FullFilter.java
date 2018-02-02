package com.example.pc.khaledwaleedshopping.products.filter;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.products.home.Home;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 10/5/2017.
 */

public class FullFilter extends Dialog {
    static CustomeTextView location;
    ImageView close;
    static Spinner colors1, colors2, brands, size, condition;
    CustomeButton filter;
    CustomeButton category;
    protected static JSONObject jsonObject = null;
    protected static int categoryNumber;
    protected static String category1_id, category2_id, category3_id, city_id, government_id;
    protected static CustomeTextView categoryTitle, textSize;
    protected static String category2Title = "", category1Title = "", govTitle = "";
    private String id;
    CrystalRangeSeekbar price;

    public FullFilter(@NonNull final FragmentActivity context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        {
            this.setContentView(R.layout.full_filter);
            textSize = (CustomeTextView) this.findViewById(R.id.text_size);
            price = (CrystalRangeSeekbar) this.findViewById(R.id.priceBar);

            close = (ImageView) this.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FullFilter.this.hide();
                }
            });

            final CustomeTextView tvMin = (CustomeTextView) this.findViewById(R.id.min);
            final CustomeTextView tvMax = (CustomeTextView) this.findViewById(R.id.max);

            // set listener
            price.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                @Override
                public void valueChanged(Number minValue, Number maxValue) {
                    if (minValue.intValue() == 1000)
                        tvMin.setText("∞");
                    else
                        tvMin.setText(String.valueOf(minValue));

                    if (maxValue.intValue() == 1000)
                        tvMax.setText("∞");
                    else
                        tvMax.setText(String.valueOf(maxValue));
                }
            });


            try {
                id = MainActivity.jsonObjectUser.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            categoryNumber = 0;
            category1_id = "-1";
            city_id = "-1";
            government_id = "-1";
            category2_id = "-1";
            category3_id = "-1";

            location = (CustomeTextView) this.findViewById(R.id.location);
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (government_id.equals("-1"))
                        new Location(context, "gov", "-1");
                    else
                        new Location(context, "city", government_id);

                }
            });
            categoryTitle = (CustomeTextView) this.findViewById(R.id.category_title);
            category = (CustomeButton) this.findViewById(R.id.category);
            filter = (CustomeButton) this.findViewById(R.id.filter);

            colors1 = (Spinner) this.findViewById(R.id.colors1);
            colors2 = (Spinner) this.findViewById(R.id.colors2);
            brands = (Spinner) this.findViewById(R.id.brands);
            size = (Spinner) this.findViewById(R.id.size);
            condition = (Spinner) this.findViewById(R.id.condition);


            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    try {
                        jsonObject = new JSONObject(result);
                        colors1.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("color"), "color"));
                        colors2.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("color"), "color"));
                        brands.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("brands"), "name"));
                        condition.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("condition"), "title"));
                        size.setVisibility(View.GONE);
                        textSize.setVisibility(View.GONE);
                        category.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new Category(context);
                            }
                        });
                        filter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                filter();
                            }
                        });
                        //  Log.e("jArray", jsonObject.get);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, context, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.addProductData, "");
        }
    }


    private void filter() {

        UrlData urlData = new UrlData();
        urlData.add("id_category1", category1_id);
        urlData.add("id_category2", category2_id);
        urlData.add("id_category3", category3_id);


        urlData.add("price_to", price.getSelectedMaxValue().toString());
        urlData.add("price_from", price.getSelectedMinValue().toString());


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

        Home.kind = urlData.get();
        this.cancel();
    }


}

