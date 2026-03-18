package dev.surma.parakeeb;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int PERM_REQ_CODE = 101;

    static {
        try {
            System.loadLibrary("c++_shared");
            System.loadLibrary("onnxruntime");
        } catch (UnsatisfiedLinkError e) {
            Log.w(TAG, "Failed to load dependencies (c++_shared or onnxruntime)", e);
        }
        System.loadLibrary("android_transcribe_app");
    }

    private TextView statusText;
    private Button grantButton;
    private View permsCard;
    private Button startSubsButton;
    private Button llmTestButton;
    private LlmSettingsStore llmSettingsStore;
    private OpenAiChatClient openAiChatClient;
    private Call inFlightLlmTestCall;
    private EditText llmBaseUrlInput;
    private EditText llmModelInput;
    private EditText llmApiKeyInput;
    private EditText llmInstructionsInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llmSettingsStore = new LlmSettingsStore(this);
        openAiChatClient = new OpenAiChatClient();

        statusText = findViewById(R.id.text_status);
        permsCard = findViewById(R.id.card_permissions);
        grantButton = findViewById(R.id.btn_grant_perms);
        startSubsButton = findViewById(R.id.btn_subs_start);
        Button imeSettingsButton = findViewById(R.id.btn_ime_settings);
        Button llmSaveButton = findViewById(R.id.btn_llm_save);
        llmTestButton = findViewById(R.id.btn_llm_test);
        llmBaseUrlInput = findViewById(R.id.input_llm_base_url);
        llmModelInput = findViewById(R.id.input_llm_model);
        llmApiKeyInput = findViewById(R.id.input_llm_api_key);
        llmInstructionsInput = findViewById(R.id.input_llm_instructions);

        grantButton.setOnClickListener(v -> checkAndRequestPermissions());

        imeSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            startActivity(intent);
        });

        startSubsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LiveSubtitleActivity.class);
            startActivity(intent);
        });

        llmSaveButton.setOnClickListener(v -> saveLlmSettings());
        llmTestButton.setOnClickListener(v -> startLlmTestConnection());

        Switch autoRecordSwitch = findViewById(R.id.switch_auto_record);
        File autoRecordFile = new File(getFilesDir(), "auto_record");
        autoRecordSwitch.setChecked(autoRecordFile.exists());
        autoRecordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    autoRecordFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to create auto_record file", e);
                }
            } else {
                autoRecordFile.delete();
            }
        });

        Switch selectTranscriptionSwitch = findViewById(R.id.switch_select_transcription);
        File selectTranscriptionFile = new File(getFilesDir(), "select_transcription");
        selectTranscriptionSwitch.setChecked(selectTranscriptionFile.exists());
        selectTranscriptionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    selectTranscriptionFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to create select_transcription file", e);
                }
            } else {
                selectTranscriptionFile.delete();
            }
        });

        Switch pauseAudioSwitch = findViewById(R.id.switch_pause_audio);
        File pauseAudioFile = new File(getFilesDir(), "pause_audio");
        pauseAudioSwitch.setChecked(pauseAudioFile.exists());
        pauseAudioSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    pauseAudioFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to create pause_audio file", e);
                }
            } else {
                pauseAudioFile.delete();
            }
        });

        loadLlmSettings();

        // Initial check
        updatePermissionUI();

        // Start init
        initNative(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inFlightLlmTestCall != null) {
            inFlightLlmTestCall.cancel();
            inFlightLlmTestCall = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionUI();
    }

    private void updatePermissionUI() {
        boolean hasAudio = checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasAudio) {
            permsCard.setVisibility(View.GONE);
        } else {
            permsCard.setVisibility(View.VISIBLE);
        }
    }

    private void checkAndRequestPermissions() {
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, PERM_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERM_REQ_CODE) {
            updatePermissionUI();
        }
    }

    private void loadLlmSettings() {
        try {
            LlmSettings settings = llmSettingsStore.read();
            llmBaseUrlInput.setText(settings.baseUrl);
            llmModelInput.setText(settings.model);
            llmApiKeyInput.setText(settings.apiKey);
            llmInstructionsInput.setText(settings.extraInstructions);
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to load LLM settings", e);
            Toast.makeText(this, R.string.toast_llm_settings_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLlmSettings() {
        try {
            llmSettingsStore.save(readLlmSettingsFromInputs());
            Toast.makeText(this, R.string.toast_llm_settings_saved, Toast.LENGTH_SHORT).show();
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to save LLM settings", e);
            Toast.makeText(this, R.string.toast_llm_settings_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void startLlmTestConnection() {
        try {
            LlmSettings settings = readLlmSettingsFromInputs();
            llmSettingsStore.save(settings);
            setLlmTestInProgress(true);
            inFlightLlmTestCall = openAiChatClient.testConnectionAsync(settings, new OpenAiChatClient.Callback() {
                @Override
                public void onSuccess(String text) {
                    runOnUiThread(() -> {
                        inFlightLlmTestCall = null;
                        setLlmTestInProgress(false);
                        Toast.makeText(MainActivity.this, R.string.toast_llm_test_success, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onFailure(String message, Throwable error) {
                    runOnUiThread(() -> {
                        inFlightLlmTestCall = null;
                        setLlmTestInProgress(false);
                        showLlmTestFailure(message);
                    });
                }

                @Override
                public void onCanceled() {
                    runOnUiThread(() -> {
                        inFlightLlmTestCall = null;
                        setLlmTestInProgress(false);
                        Toast.makeText(MainActivity.this, R.string.toast_llm_test_canceled, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to start LLM connection test", e);
            setLlmTestInProgress(false);
            Toast.makeText(this, R.string.toast_llm_settings_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setLlmTestInProgress(boolean inProgress) {
        llmTestButton.setEnabled(!inProgress);
        llmTestButton.setText(inProgress ? R.string.btn_llm_testing : R.string.btn_llm_test);
        llmTestButton.setAlpha(inProgress ? 0.7f : 1.0f);
    }

    private void showLlmTestFailure(String message) {
        String text = getString(R.string.toast_llm_test_failure);
        if (message != null && !message.isEmpty()) {
            text = text + ": " + message;
        }
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private LlmSettings readLlmSettingsFromInputs() {
        return new LlmSettings(
                llmBaseUrlInput.getText().toString(),
                llmApiKeyInput.getText().toString(),
                llmModelInput.getText().toString(),
                llmInstructionsInput.getText().toString());
    }

    // Called from Rust
    public void onStatusUpdate(String status) {
        runOnUiThread(() -> {
            statusText.setText("Status: " + status);
            if ("Ready".equals(status)) {
                startSubsButton.setEnabled(true);
            }
        });
    }

    private native void initNative(MainActivity activity);
}
