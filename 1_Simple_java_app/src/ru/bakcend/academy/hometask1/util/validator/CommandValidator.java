package ru.bakcend.academy.hometask1.util.validator;

public class CommandValidator {

    private CommandValidator() {
    }

    public static String extractProductName(String[] parts) {
        StringBuilder nameBuilder = new StringBuilder();
        if (!parts[2].startsWith("\"")) {
            return parts[2];
        }
        boolean inQuotes = false;

        for (int i = 2; i < parts.length - 2; i++) {
            if (inQuotes) {
                nameBuilder.append(" ").append(parts[i]);
            } else {
                if (parts[i].startsWith("\"")) {
                    inQuotes = true;
                    nameBuilder.append(parts[i].substring(1));
                } else {
                    return null;
                }
            }

            if (parts[i].endsWith("\"")) {
                inQuotes = false;
                nameBuilder.deleteCharAt(nameBuilder.length() - 1);
                nameBuilder.append(" ");
            }
        }

        if (inQuotes) {
            return null;
        }

        return nameBuilder.toString().trim();
    }

    public static boolean isValidPrice(String priceStr) {
        try {
            float price = Float.parseFloat(priceStr);
            return price >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidQuantity(String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr);
            return quantity >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidProductItemNumber(String itemNumber) {
        return itemNumber.matches("[A-Z0-9]+");
    }


}





