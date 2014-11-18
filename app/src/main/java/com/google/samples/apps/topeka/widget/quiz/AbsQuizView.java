/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.samples.apps.topeka.widget.quiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.DimenRes;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.samples.apps.topeka.R;
import com.google.samples.apps.topeka.activity.QuizActivity;
import com.google.samples.apps.topeka.model.Category;
import com.google.samples.apps.topeka.model.Theme;
import com.google.samples.apps.topeka.model.quiz.Quiz;
import com.google.samples.apps.topeka.widget.DoneFab;
import com.google.samples.apps.topeka.widget.FloatingActionButton;

/**
 * This is the base class for displaying a {@link com.google.samples.apps.topeka.model.quiz.Quiz}.
 * <p>
 * Subclasses need to implement {@link AbsQuizView#getQuizContentView()}
 * in order to allow solution of a quiz.
 * </p>
 * <p>
 * Also {@link AbsQuizView#setAnswered(boolean)} needs to be called with
 * <code>true</code> in order to mark the quiz solved.
 * </p>
 * @param <Q> The type of {@link com.google.samples.apps.topeka.model.quiz.Quiz} you want to
 *            display.
 */
public abstract class AbsQuizView<Q extends Quiz> extends CardView implements
        View.OnClickListener {

    private final Category mCategory;
    private TextView mQuestionView;
    private FloatingActionButton mSubmitAnswer;
    private Q mQuiz;
    private boolean mAnswered;
    private int mDefaultPadding;

    public AbsQuizView(Context context, Category category, Q quiz) {
        super(context);
        mQuiz = quiz;
        mDefaultPadding = getResources().getDimensionPixelSize(R.dimen.padding_default);
        mCategory = category;
        mSubmitAnswer = getSubmitButton(context);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);

        setupQuestionView(mCategory);

        //modify the quiz content view
        View quizContentView = getQuizContentView();
        setDefaultPadding(quizContentView);
        setMinHeightInternal(quizContentView, R.dimen.min_height_question);

        //add them to the container
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        container.addView(mQuestionView, layoutParams);
        container.addView(quizContentView, layoutParams);
        addView(container, layoutParams);
        //create and attach submit button

        final int fabSize = getResources().getDimensionPixelSize(R.dimen.fab_size);
        addView(mSubmitAnswer,
                new LayoutParams(fabSize, fabSize, Gravity.END | Gravity.CENTER_VERTICAL));
    }

    private FloatingActionButton getSubmitButton(Context context) {
        if (null == mSubmitAnswer) {
            mSubmitAnswer = new DoneFab(context);
            mSubmitAnswer.setId(R.id.submitAnswer);
            mSubmitAnswer.setVisibility(GONE);
            //Set QuizActivity to handle clicks on answer submission.
            if (context instanceof QuizActivity) {
                mSubmitAnswer.setOnClickListener(this);
            }
        }
        return mSubmitAnswer;
    }

    private void setupQuestionView(Category category) {
        mQuestionView = new TextView(getContext());
        int textColor = getResources().getColor(category.getTheme().getTextPrimaryColor());
        int backgroundColor = getResources().getColor(category.getTheme().getPrimaryColor());
        mQuestionView
                .setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Subhead);
        mQuestionView.setBackgroundColor(backgroundColor);
        mQuestionView.setTextColor(textColor);
        mQuestionView.setGravity(Gravity.CENTER_VERTICAL);
        setDefaultPadding(mQuestionView);
        setMinHeightInternal(mQuestionView, R.dimen.min_height_question);
        mQuestionView.setText(getQuiz().getQuestion());
    }

    private void setDefaultPadding(View view) {
        view.setPadding(mDefaultPadding, mDefaultPadding, mDefaultPadding, mDefaultPadding);
    }

    /**
     * Implementations should create the content view for the type of
     * {@link com.google.samples.apps.topeka.model.quiz.Quiz} they want to display.
     *
     * @return the created view to solve the quiz.
     */
    protected abstract View getQuizContentView();

    public Q getQuiz() {
        return mQuiz;
    }

    protected boolean isAnswered() {
        return mAnswered;
    }

    /**
     * Sets the quiz to answered or unanswered.
     *
     * @param answered <code>true</code> if an answer was selected, else <code>false</code>.
     */
    protected void setAnswered(final boolean answered) {
        if (null != mSubmitAnswer) {
            mSubmitAnswer.setVisibility(answered ? View.VISIBLE : View.GONE);
            mAnswered = answered;
        }
    }

    /**
     * Sets the quiz to answered if it not already has been answered.
     * Otherwise does nothing.
     */
    protected void answerQuiz() {
        if (!isAnswered()) {
            setAnswered(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitAnswer: {
                //TODO: 11/3/14 handle
                if (getContext() instanceof QuizActivity) {
                    ((QuizActivity) getContext()).onClick(v);
                }
                //perform answering in mCategory
                break;
            }
        }
    }

    protected void setMinHeightForTouchTarget(View view) {
        setMinHeightInternal(view, R.dimen.min_height_touch_target);
    }

    private void setMinHeightInternal(View view, @DimenRes int resId) {
        view.setMinimumHeight(getResources().getDimensionPixelSize(resId));
    }
}
