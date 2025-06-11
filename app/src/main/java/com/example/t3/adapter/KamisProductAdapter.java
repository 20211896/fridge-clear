package com.example.t3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.t3.R;
import com.example.t3.manager.BasketManager;
import com.example.t3.model.BasketItem;
import com.example.t3.model.KamisProduct;
import com.example.t3.ui.ProductDetailActivity;
import com.example.t3.utils.CustomToast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KamisProductAdapter extends RecyclerView.Adapter<KamisProductAdapter.KamisProductViewHolder> {

    private List<KamisProduct> products = new ArrayList<>();
    private OnKamisProductClickListener listener;

    public interface OnKamisProductClickListener {
        void onAddToCartClick(KamisProduct product);
    }

    public void setOnProductClickListener(OnKamisProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<KamisProduct> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KamisProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kamis_product, parent, false);
        return new KamisProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KamisProductViewHolder holder, int position) {
        KamisProduct product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class KamisProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView txtName, txtPrice, txtCategory, txtUnit, txtGrade;
        Button btnAddCart;

        public KamisProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.img_kamis_category);
            txtName = itemView.findViewById(R.id.txt_kamis_name);
            txtPrice = itemView.findViewById(R.id.txt_kamis_price);
            txtCategory = itemView.findViewById(R.id.txt_kamis_category);
            txtUnit = itemView.findViewById(R.id.txt_kamis_unit);
            txtGrade = itemView.findViewById(R.id.txt_kamis_grade);
            btnAddCart = itemView.findViewById(R.id.btn_kamis_add_cart);
        }

        public void bind(KamisProduct product) {
            txtName.setText(product.getFullName());
            txtPrice.setText(product.getFormattedPrice());
            txtCategory.setText(product.getCategoryName());
            txtUnit.setText(product.getUnitName());
            txtGrade.setText(product.getRankName());

            // 실제 농산물 이미지 로드
            loadProductImage(product);

            // 🆕 카드 전체 클릭 리스너 추가 - 상품 상세 페이지로 이동
            itemView.setOnClickListener(v -> {
                ProductDetailActivity.start(itemView.getContext(), product);
            });

            // 🎯 장바구니 추가 버튼 클릭 (이미지 URL 포함)
            btnAddCart.setOnClickListener(v -> {
                if (listener != null) {
                    // Fragment에서 처리하는 경우
                    listener.onAddToCartClick(product);
                } else {
                    // 기본 장바구니 추가 처리 (이미지 URL 포함)
                    addToCartWithImage(product);
                }
            });
        }

        /**
         * 🎯 이미지 URL을 포함한 장바구니 추가 처리
         */
        private void addToCartWithImage(KamisProduct product) {
            // BasketManager를 통해 장바구니에 추가
            BasketManager basketManager = BasketManager.getInstance(itemView.getContext());

            // BasketItem 생성 (이미지 URL 포함)
            String id = String.valueOf(System.currentTimeMillis());
            String name = product.getFullName();
            int unitPrice = (int) Math.round(product.getPriceAsDouble());
            int quantity = 1;
            String imageUrl = product.getImageUrl(); // 🎯 상품의 이미지 URL 포함

            BasketItem basketItem = new BasketItem(id, name, unitPrice, quantity, imageUrl);
            basketManager.addMyBasketItem(basketItem);

            // 성공 메시지 표시 (상세한 정보 포함)
            NumberFormat fmt = NumberFormat.getNumberInstance(Locale.KOREA);
            CustomToast.show(itemView.getContext(),
                    "🛒 " + product.getFullName() + "\n" +
                            "수량: " + quantity + "개\n" +
                            "가격: " + fmt.format(unitPrice) + "원\n" +
                            "장바구니에 추가되었습니다!");
        }

        /**
         * 상품 이미지 로딩 (개선된 버전)
         */
        private void loadProductImage(KamisProduct product) {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                // Unsplash 이미지 로딩
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_menu_gallery)
                                .error(R.drawable.ic_menu_gallery)
                                .centerCrop())
                        .into(imgCategory);

                // 이미지가 있을 때는 배경색 제거
                imgCategory.setBackgroundColor(0x00000000);
            } else {
                // 이미지가 없으면 카테고리별 색상 배경 표시
                setCategoryIcon(product.getCategoryName());
            }
        }

        /**
         * 카테고리별 아이콘과 배경색 설정
         */
        private void setCategoryIcon(String category) {
            switch (category) {
                case "식량작물":
                    imgCategory.setBackgroundColor(0xFFFFEB3B); // 노란색
                    break;
                case "채소류":
                    imgCategory.setBackgroundColor(0xFF4CAF50); // 초록색
                    break;
                case "과일류":
                    imgCategory.setBackgroundColor(0xFFFF9800); // 주황색
                    break;
                case "축산물":
                    imgCategory.setBackgroundColor(0xFFF44336); // 빨간색
                    break;
                case "수산물":
                    imgCategory.setBackgroundColor(0xFF2196F3); // 파란색
                    break;
                default:
                    imgCategory.setBackgroundColor(0xFF9E9E9E); // 회색
                    break;
            }

            // 기본 아이콘 설정
            imgCategory.setImageResource(R.drawable.ic_menu_gallery);
            imgCategory.setScaleType(ImageView.ScaleType.CENTER);
        }
    }
}