package com.example.powermeter7;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = findViewById(R.id.aboutText);
        ImageView backButton = findViewById(R.id.backButton);

        // Enable scrolling manually on TextView (even inside ScrollView)
        aboutText.setMovementMethod(new ScrollingMovementMethod());

        aboutText.setText(
                "🔋 SMART INSIGHTS FOR ENERGY SAVING 🔋\n\n" +

                        "1️⃣ Detect Idle Power Usage:\n" +
                        "• Logic: Monitor power < 5W for > 10 mins.\n" +
                        "• Implementation: Store timestamp & check power levels in Firebase.\n\n" +

                        "2️⃣ Overcharging Alerts:\n" +
                        "• Logic: If device charging power drops to < 2W but still plugged.\n" +
                        "• Implementation: ESP32 checks if current ≈ 0, send Firebase alert.\n\n" +

                        "3️⃣ Peak Hour Detection:\n" +
                        "• Logic: Analyze usage vs time.\n" +
                        "• Implementation: Use daily logs in `/powerData/energyLog/` & show graph.\n\n" +

                        "4️⃣ Daily Energy Goal:\n" +
                        "• Logic: User sets target in app, monitor kWh live.\n" +
                        "• Implementation: Compare current `/powerData/kWh` to goal, notify user.\n\n" +

                        "5️⃣ Power Factor Monitoring:\n" +
                        "• Logic: PF < 0.85 = inefficient load.\n" +
                        "• Implementation: Show real-time PF, highlight in red if poor.\n\n" +

                        "6️⃣ Energy-Saving Tips:\n" +
                        "• Display static or dynamic tips.\n" +
                        "• Can be loaded from Firebase `/tips/` node and shown randomly.\n\n" +

                        "7️⃣ Room Temperature Effect:\n" +
                        "• Logic: High temps increase device inefficiency.\n" +
                        "• Implementation: Add DHT11/DHT22 sensor to ESP32 and log `/temperature/`.\n\n" +

                        "8️⃣ Weekly Consumption Trends:\n" +
                        "• Logic: Aggregate daily data from `energyLog`.\n" +
                        "• Implementation: GraphActivity plots 7-day line chart.\n\n" +

                        "9️⃣ Device-wise Usage (Optional):\n" +
                        "• Logic: Use smart plugs or relays per device.\n" +
                        "• Implementation: Each plug logs its own current/power to `/devices/{id}/`\n\n" +

                        "🔧 All features rely on:\n" +
                        "• Firebase Real-Time Database\n" +
                        "• ESP32 + SCT-013 & ZMPT101B\n" +
                        "• Android Java App + MPAndroidChart\n\n" +

                        "📈 Aim: Save Energy. Cut Costs. Stay Smart.\n"
        );

        backButton.setOnClickListener(v -> finish());
    }
}
