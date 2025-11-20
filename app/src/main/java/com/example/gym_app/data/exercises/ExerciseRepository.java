package com.example.gym_app.data.exercises;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gym_app.R;
import com.example.gym_app.data.auth.AuthSessionManager;
import com.example.gym_app.model.Exercise;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class ExerciseRepository {

    private final ExerciseRemoteDataSource remoteDataSource;
    private final AuthSessionManager authSessionManager;
    private final List<Call<?>> ongoingCalls = new ArrayList<>();

    public ExerciseRepository() {
        this(new ExerciseRemoteDataSource(), new AuthSessionManager());
    }

    ExerciseRepository(ExerciseRemoteDataSource remoteDataSource,
                       AuthSessionManager authSessionManager) {
        this.remoteDataSource = remoteDataSource;
        this.authSessionManager = authSessionManager;
    }

    public void getExercisesByRoutine(Context context,
                                      Long routineId,
                                      final GetExercisesCallback callback) {
        if (context == null || routineId == null || callback == null) {
            return;
        }
        String authToken = authSessionManager.getAuthToken(context);
        Call<List<ExerciseResponse>> call = remoteDataSource.getExercises(authToken, new ExerciseRemoteDataSource.GetExercisesCallback() {
            @Override
            public void onSuccess(List<ExerciseResponse> exercises) {
                ongoingCalls.remove(call);
                callback.onSuccess(filterAndMap(exercises, routineId));
            }

            @Override
            public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                ongoingCalls.remove(call);
                callback.onError(resolveError(context, errorMessage, throwable));
            }
        });
        ongoingCalls.add(call);
    }

    public void createExercise(Context context,
                               ExerciseRequest request,
                               final ExerciseMutationCallback callback) {
        if (context == null || request == null || callback == null) {
            return;
        }
        String authToken = authSessionManager.getAuthToken(context);
        Call<ExerciseResponse> call = remoteDataSource.createExercise(authToken, request, new ExerciseRemoteDataSource.CreateExerciseCallback() {
            @Override
            public void onSuccess(ExerciseResponse exercise) {
                ongoingCalls.remove(call);
                callback.onSuccess(mapExercise(exercise));
            }

            @Override
            public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                ongoingCalls.remove(call);
                callback.onError(resolveError(context, errorMessage, throwable));
            }
        });
        ongoingCalls.add(call);
    }

    public void updateExercise(Context context,
                               Long exerciseId,
                               ExerciseRequest request,
                               final ExerciseMutationCallback callback) {
        if (context == null || exerciseId == null || request == null || callback == null) {
            return;
        }
        String authToken = authSessionManager.getAuthToken(context);
        Call<ExerciseResponse> call = remoteDataSource.updateExercise(authToken, exerciseId, request,
                new ExerciseRemoteDataSource.UpdateExerciseCallback() {
                    @Override
                    public void onSuccess(ExerciseResponse exercise) {
                        ongoingCalls.remove(call);
                        callback.onSuccess(mapExercise(exercise));
                    }

                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCalls.remove(call);
                        callback.onError(resolveError(context, errorMessage, throwable));
                    }
                });
        ongoingCalls.add(call);
    }

    public void deleteExercise(Context context,
                               Long exerciseId,
                               final DeleteExerciseCallback callback) {
        if (context == null || exerciseId == null || callback == null) {
            return;
        }
        String authToken = authSessionManager.getAuthToken(context);
        Call<Void> call = remoteDataSource.deleteExercise(authToken, exerciseId, new ExerciseRemoteDataSource.DeleteExerciseCallback() {
            @Override
            public void onSuccess() {
                ongoingCalls.remove(call);
                callback.onSuccess();
            }

            @Override
            public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                ongoingCalls.remove(call);
                callback.onError(resolveError(context, errorMessage, throwable));
            }
        });
        ongoingCalls.add(call);
    }

    public void cancelOngoingCall() {
        for (Call<?> call : new ArrayList<>(ongoingCalls)) {
            if (call != null) {
                call.cancel();
            }
        }
        ongoingCalls.clear();
    }

    private List<Exercise> filterAndMap(List<ExerciseResponse> responses, Long routineId) {
        if (responses == null || responses.isEmpty()) {
            return Collections.emptyList();
        }
        List<Exercise> exercises = new ArrayList<>();
        for (ExerciseResponse response : responses) {
            if (response != null && routineId.equals(response.getRoutineId())) {
                exercises.add(mapExercise(response));
            }
        }
        return exercises;
    }

    private Exercise mapExercise(@Nullable ExerciseResponse response) {
        if (response == null) {
            return null;
        }
        return new Exercise(
                response.getId(),
                response.getName(),
                response.getSets(),
                response.getRepetitions(),
                response.getRestTime(),
                response.getWeightType(),
                response.getRoutineId()
        );
    }

    private String resolveError(Context context,
                                @Nullable String errorMessage,
                                @Nullable Throwable throwable) {
        if (throwable instanceof IOException) {
            return context.getString(R.string.trainer_routine_create_error_network);
        }
        if (!TextUtils.isEmpty(errorMessage)) {
            return errorMessage;
        }
        return context.getString(R.string.trainer_routine_create_error_generic);
    }

    public interface GetExercisesCallback {
        void onSuccess(List<Exercise> exercises);
        void onError(@NonNull String errorMessage);
    }

    public interface ExerciseMutationCallback {
        void onSuccess(@Nullable Exercise exercise);
        void onError(@NonNull String errorMessage);
    }

    public interface DeleteExerciseCallback {
        void onSuccess();
        void onError(@NonNull String errorMessage);
    }
}