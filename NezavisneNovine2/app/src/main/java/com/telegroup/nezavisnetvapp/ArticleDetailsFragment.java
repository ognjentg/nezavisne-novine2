/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.telegroup.nezavisnetvapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.app.DetailsFragmentBackgroundController;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Picasso;
import com.telegroup.nezavisnetvapp.util.BlurTransform;
import com.telegroup.nezavisnetvapp.util.BlurTransformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class ArticleDetailsFragment extends DetailsFragment {
    private static final String TAG = "ArticleDetailsFragment";



    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;


    private NewsCard mSelectedArticle;
    private Article mRealArticle=new Article();
    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private DetailsFragmentBackgroundController mDetailsBackground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mDetailsBackground = new DetailsFragmentBackgroundController(this);

        mSelectedArticle =
                (NewsCard) getActivity().getIntent().getSerializableExtra(DetailsActivity.Article);
        if (mSelectedArticle != null) {
            String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

            JsonObjectRequest jsonObjectReq = new JsonObjectRequest("http://dtp.nezavisne.com/app/v2/vijesti/" + mSelectedArticle.getNewsId(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                mRealArticle.setId(Integer.parseInt(response.getString("vijestID")));
                                mRealArticle.setTitle(response.getString("Naslov"));
                                mRealArticle.setDescription("Autor: "+response.getString("Autor")+"     Datum: "+response.getString("Datum")+"\n\n"+response.getString("Lid"));
                                mRealArticle.setImageUrl(response.getJSONArray("Slika").getJSONObject(0).getString("slikaURL").replace("555x333","750x450"));
                                mRealArticle.setCategoryId(response.getString("meniRoditelj"));
                                mRealArticle.setBody(parseNewsContent(response.getString("Tjelo")));
                                mPresenterSelector = new ClassPresenterSelector();
                                mAdapter = new ArrayObjectAdapter(mPresenterSelector);
                                System.out.println(mRealArticle.getTitle());
                                System.out.println(mRealArticle.getBody());
                                setupDetailsOverviewRow();
                                setupDetailsOverviewRowPresenter();
                                setupRelatedArticleListRow();
                                setAdapter(mAdapter);
                                initializeBackground(mRealArticle);
                                setOnItemViewClickedListener(new ItemViewClickedListener());
                            } catch (JSONException e) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding JsonObject request to request queue
            AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

        }
    }

    private void initializeBackground(Article data) {
        mDetailsBackground.enableParallax();

        Glide.with(getActivity())
                .load((data.getImageUrl())).asBitmap()
                .centerCrop()
                .error(R.drawable.default_background1)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        mDetailsBackground.setCoverBitmap(bitmap);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });
    }
    public int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private void setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mRealArticle.toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mRealArticle);
        row.setImageDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.red));
        int width = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);

      //  Bitmap blurredBitmap = BlurBuilder.blur( getActivity(),);
       // Picasso.with(getActivity()).load(mRealArticle.getImageUrl()).transform(BlurTransform.
         //       getInstance(this.getActivity())).fit().into();

        Glide.with(getActivity())

                .load(mRealArticle.getImageUrl()).transform(new BlurTransformation(getActivity()))

                .centerCrop()
                .error(R.drawable.default_background1)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Log.d(TAG, "details overview card image url ready: " + resource);
                        row.setImageDrawable(resource);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });
        mAdapter.add(row);
    }



    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        FullWidthDetailsOverviewRowPresenter detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(
                ContextCompat.getColor(getActivity(), R.color.default_background));

        // Hook up transition element.
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper =
                new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(
                getActivity(), DetailsActivity.SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {

            }
        });

        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupRelatedArticleListRow() {


//
        final JsonArrayRequest jsonArray = new JsonArrayRequest("http://dtp.nezavisne.com/app/rubrika/" + mRealArticle.getCategoryId() + "/10/20",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
                        for (int i = 0; i < response.length(); i++)
                            try {

                                NewsCard  card=new NewsCard(response.getJSONObject(i).getString("Naslov"),
                                        response.getJSONObject(i).getString("vijestID"),
                                        response.getJSONObject(i).getString("Lid"),
                                        response.getJSONObject(i).getString("Slika"),
                                        response.getJSONObject(i).getString("Datum"),
                                        response.getJSONObject(i).getString("Autor"),
                                        response.getJSONObject(i).getString("meniID"));
                                if (!card.getNewsId().equals(mRealArticle.getId()+""))
                                listRowAdapter.add(card);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        HeaderItem header = new HeaderItem(0, "Povezane vijesti");
                        mAdapter.add(new ListRow(header, listRowAdapter));
                        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArray,  "com.androidtutorialpoint.volleyJsonArrayRequest");

    }



    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof NewsCard) {
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("Article",(NewsCard) item);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }}

    private String parseNewsContent(String content) {
        Document doc = Jsoup.parse(content);

        doc.select("blockquote").remove();

        String whiteListElements = "p";  //blockquote
        String[] whiteListArray = whiteListElements.split(",");

        Whitelist whitelist = new Whitelist();
        for (String tag : whiteListArray)
            whitelist.addTags(tag);

        return Jsoup.clean(doc.toString(), whitelist).replace("&nbsp;"," ").replace("<p>","\n").replace("</p>","");
    }
}
