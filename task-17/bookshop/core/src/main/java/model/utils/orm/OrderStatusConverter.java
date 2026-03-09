package model.utils.orm;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import model.enums.OrderStatus;
import model.status.IOrderStatus;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<IOrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(IOrderStatus iOrderStatus) {
        if (iOrderStatus == null)
            return null;
        else {
            return iOrderStatus.toString();
        }
    }

    @Override
    public IOrderStatus convertToEntityAttribute(String s) {
        if (s == null)
            return null;
        else {
            return IOrderStatus.from(OrderStatus.valueOf(s));
        }
    }
}
