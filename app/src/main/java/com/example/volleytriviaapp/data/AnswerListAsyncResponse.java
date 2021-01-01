package com.example.volleytriviaapp.data;

import com.example.volleytriviaapp.model.Question;

import java.util.ArrayList;

public interface AnswerListAsyncResponse {
    void processFinished(ArrayList<Question> questionArrayList);
}
