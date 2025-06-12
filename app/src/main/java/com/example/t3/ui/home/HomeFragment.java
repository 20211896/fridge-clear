package com.example.t3.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

import com.example.t3.R;
import com.example.t3.databinding.FragmentHomeBinding;
import com.example.t3.utils.CustomToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GridLayout gridLayout;
    private int buttonCount = 0;
    private SharedPreferences sharedPreferences;
    private Button sortButton;
    private static final long DAY_MILLIS = 24L * 60 * 60 * 1000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        gridLayout = binding.gridLayout;
        sortButton = binding.sortButton;
        Button addButton = binding.addButton;

        sharedPreferences = requireContext()
                .getSharedPreferences("fridge_data", Context.MODE_PRIVATE);

        buttonCount = sharedPreferences.getInt("button_count", 0);
        restoreSavedButtons();
        cleanupUnusedData();

        // 정렬 버튼
        sortButton.setOnClickListener(v -> {
            String[] opts = {"기본", "유통날짜 빠른순"};
            new AlertDialog.Builder(requireContext())
                    .setTitle("정렬 방식 선택")
                    .setItems(opts, (d, which) -> {
                        if (which == 0) restoreDefaultOrder();
                        else sortByExpiration();
                    })
                    .show();
        });

        addButton.setOnClickListener(v -> addNewCell());

        return root;
    }

    // 1. 저장된 버튼 복원
    private void restoreSavedButtons() {
        for (int i = 1; i <= buttonCount; i++) {
            createButton(i);
        }
    }

    // 2. 새 칸 추가
    private void addNewCell() {
        buttonCount++;
        createButton(buttonCount);
        saveButtonCount();
        CustomToast.show(requireContext(), "칸" + buttonCount + " 추가");
    }

    // 3. 버튼 생성: 높이 80dp, 흰 배경+검은 테두리, 그림자
    private void createButton(int num) {
        Button b = new Button(requireContext());
        b.setTag(num);

        int hPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0; p.height = hPx;
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        p.setMargins(8, 8, 8, 8);
        b.setLayoutParams(p);

        b.setBackground(createCellBackground());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float elev = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            b.setElevation(elev);
        }

        setupButtonTextStyle(b);
        updateButtonText(b, num);

        b.setOnClickListener(v -> openFoodDetail(num));
        b.setOnLongClickListener(v -> {
            showDeleteDialog(b, num);
            return true;
        });

        gridLayout.addView(b);
    }

    // 4. 삭제 확인 다이얼로그
    private void showDeleteDialog(Button b, int n) {
        new AlertDialog.Builder(requireContext())
                .setTitle("칸 삭제")
                .setMessage("칸" + n + "을(를) 삭제하시겠습니까?")
                .setPositiveButton("삭제", (d, w) -> deleteButton(b, n))
                .setNegativeButton("취소", null)
                .show();
    }

    // 5. 삭제 + 재배치 (애니메이션 정리 추가)
    private void deleteButton(Button b, int delNum) {
        // 삭제할 버튼의 애니메이션 정리
        b.clearAnimation();

        deleteCellData(delNum);
        gridLayout.removeView(b);

        Map<Integer, Map<String, String>> tmp = new HashMap<>();
        for (int i = 1; i <= buttonCount; i++) {
            if (i == delNum) continue;
            Map<String,String> cell = new HashMap<>();
            String[] keys = {
                    "food","buy_date","end_date","amount","image",
                    "memo","category","location","alarm","favorite","notes"
            };
            for (String k : keys) {
                cell.put(k, sharedPreferences.getString("cell_" + i + "_" + k, ""));
            }
            tmp.put(i, cell);
        }

        // 모든 데이터 삭제
        SharedPreferences.Editor edAll = sharedPreferences.edit();
        for (int i = 1; i <= buttonCount; i++) {
            for (String k : tmp.get(tmp.keySet().iterator().next()).keySet()) {
                edAll.remove("cell_" + i + "_" + k);
            }
        }
        edAll.apply();

        // 재번호 매김
        int newNum = 1;
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View c = gridLayout.getChildAt(i);
            if (c instanceof Button) {
                Button nb = (Button) c;
                // 기존 애니메이션 정리
                nb.clearAnimation();

                int orig = (Integer) nb.getTag();
                nb.setTag(newNum);

                Map<String,String> data = tmp.get(orig);
                if (data != null) {
                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    for (Map.Entry<String,String> en : data.entrySet()) {
                        if (!en.getValue().isEmpty()) {
                            ed.putString("cell_" + newNum + "_" + en.getKey(), en.getValue());
                        }
                    }
                    ed.apply();
                }

                nb.setBackground(createCellBackground());
                setupButtonTextStyle(nb);
                updateButtonText(nb, newNum);

                final int index = newNum;
                nb.setOnClickListener(v -> openFoodDetail(index));
                nb.setOnLongClickListener(v -> {
                    showDeleteDialog(nb, index);
                    return true;
                });

                newNum++;
            }
        }

        buttonCount--;
        saveButtonCount();
        CustomToast.show(requireContext(), "칸" + delNum + " 삭제");
    }

    // 6. 데이터 삭제
    private void deleteCellData(int num) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        String[] keys = {
                "food","buy_date","end_date","amount","image",
                "memo","category","location","alarm","favorite","notes"
        };
        for (String k : keys) {
            ed.remove("cell_" + num + "_" + k);
        }
        ed.apply();
    }

    // 7. 텍스트 스타일 설정
    private void setupButtonTextStyle(Button b) {
        b.setGravity(android.view.Gravity.CENTER);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        b.setMaxLines(1);
        b.setEllipsize(android.text.TextUtils.TruncateAt.END);
        b.setPadding(4,4,4,4);
        b.setIncludeFontPadding(false);
        b.setSingleLine(true);
    }

    // 8. 텍스트·테두리·배경·깜빡임 처리
    private void updateButtonText(Button btn, int cellNumber) {
        String food    = sharedPreferences.getString("cell_" + cellNumber + "_food", "");
        String endDate = sharedPreferences.getString("cell_" + cellNumber + "_end_date", "");
        String text    = food.isEmpty()
                ? "칸" + cellNumber
                : food + (endDate.isEmpty() ? "" : " " + formatShortDate(endDate));
        btn.setText(text);

        GradientDrawable bg = (GradientDrawable) btn.getBackground();
        int strokeWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        // 만료 여부 판단
        boolean isExpired = false;
        if (!endDate.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date exp = sdf.parse(endDate);
                if (exp.getTime() < System.currentTimeMillis()) {
                    isExpired = true;
                }
            } catch (ParseException ignored) { }
        }

        if (isExpired) {
            // 깜빡임 경고 스타일
            int redDark = ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark);
            bg.setColor(redDark);
            bg.setStroke(4, redDark);
            btn.setTextColor(Color.WHITE);
            btn.setTypeface(null, Typeface.BOLD);

            AlphaAnimation blink = new AlphaAnimation(1.0f, 0.3f);
            blink.setDuration(500);
            blink.setRepeatMode(AlphaAnimation.REVERSE);
            blink.setRepeatCount(AlphaAnimation.INFINITE);
            btn.startAnimation(blink);
            return;
        } else {
            // 깜빡임 해제
            btn.clearAnimation();
            btn.setTypeface(null, Typeface.NORMAL);
        }

        // 만료되지 않은 경우: 색 분기
        int borderColor;
        if (food.isEmpty()) {
            borderColor = ContextCompat.getColor(requireContext(), android.R.color.black);
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date exp = sdf.parse(endDate);
                long diff = exp.getTime() - System.currentTimeMillis();
                if (diff <= DAY_MILLIS) {
                    borderColor = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light);
                } else if (diff <= 3*DAY_MILLIS) {
                    borderColor = ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark);
                } else if (diff <= 7*DAY_MILLIS) {
                    borderColor = ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light);
                } else {
                    borderColor = ContextCompat.getColor(requireContext(), R.color.green);
                }
            } catch (Exception e) {
                borderColor = ContextCompat.getColor(requireContext(), android.R.color.black);
            }
        }

        // 은은한 배경: 흰색80% + borderColor20%
        int white = ContextCompat.getColor(requireContext(), android.R.color.white);
        int bgColor = ColorUtils.blendARGB(white, borderColor, 0.2f);

        bg.setStroke(strokeWidth, borderColor);
        bg.setColor(bgColor);
        btn.setTextColor(borderColor);
    }

    // 9. 날짜 포맷
    private String formatShortDate(String full) {
        if (full.length() >= 10) {
            return full.substring(5,7) + "/" + full.substring(8,10);
        }
        return full;
    }

    // 10. 기본 정렬 복원
    private void restoreDefaultOrder() {
        List<View> lst = new ArrayList<>();
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            lst.add(gridLayout.getChildAt(i));
        }
        lst.sort(Comparator.comparingInt(v -> (Integer)v.getTag()));
        gridLayout.removeAllViews();
        for (View v : lst) gridLayout.addView(v);
    }

    // 11. 유통날짜 빠른순 정렬
    private void sortByExpiration() {
        List<Button> btns = new ArrayList<>();
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View c = gridLayout.getChildAt(i);
            if (c instanceof Button) btns.add((Button)c);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        btns.sort((b1,b2)->{
            int t1=(Integer)b1.getTag(), t2=(Integer)b2.getTag();
            String d1=sharedPreferences.getString("cell_"+t1+"_end_date",""),
                    d2=sharedPreferences.getString("cell_"+t2+"_end_date","");
            try {
                Date dt1 = d1.isEmpty()? new Date(Long.MAX_VALUE) : sdf.parse(d1);
                Date dt2 = d2.isEmpty()? new Date(Long.MAX_VALUE) : sdf.parse(d2);
                return dt1.compareTo(dt2);
            } catch (ParseException e) {
                return Integer.compare(t1,t2);
            }
        });
        gridLayout.removeAllViews();
        for (Button b:btns) gridLayout.addView(b);
    }

    // 12. 사용하지 않는 데이터 정리
    private void cleanupUnusedData() {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        for (int i = buttonCount+1; i <= buttonCount+100; i++) {
            String[] keys = {
                    "food","buy_date","end_date","amount","image",
                    "memo","category","location","alarm","favorite","notes"
            };
            for (String k:keys) {
                String key = "cell_"+i+"_"+k;
                if (sharedPreferences.contains(key)) ed.remove(key);
            }
        }
        ed.apply();
    }

    // 13. 상세 화면 이동
    private void openFoodDetail(int num) {
        Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
        intent.putExtra("cell_number", num);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 돌아올 때마다 리프레시
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View c = gridLayout.getChildAt(i);
            if (c instanceof Button) {
                Button b = (Button)c;
                setupButtonTextStyle(b);
                updateButtonText(b, (Integer)b.getTag());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Drawable 생성: 흰 배경+검정 테두리
    private GradientDrawable createCellBackground() {
        int stroke = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        gd.setStroke(stroke, ContextCompat.getColor(requireContext(), android.R.color.black));
        return gd;
    }

    // 버튼 개수 저장
    private void saveButtonCount() {
        sharedPreferences.edit()
                .putInt("button_count", buttonCount)
                .apply();
    }
}
