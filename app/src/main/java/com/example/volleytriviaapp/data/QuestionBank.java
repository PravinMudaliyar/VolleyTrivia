package com.example.volleytriviaapp.data;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.volleytriviaapp.controller.AppController;
import com.example.volleytriviaapp.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {

    private static ArrayList<Question> questionArrayList;
    private static String url;
   // private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";
   // ArrayList<Question> questionArrayList = new ArrayList<>();

    public static List<Question> getQuestions(final AnswerListAsyncResponse callBack) {
        url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";
       questionArrayList = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, (JSONArray) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {

                    try {
                        Question question = new Question();
                        question.setQuestion(response.getJSONArray(i).get(0).toString());
                        question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));

                        questionArrayList.add(question);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (null != callBack) callBack.processFinished(questionArrayList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);


        return questionArrayList;
    }
}
