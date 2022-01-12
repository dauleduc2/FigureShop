package product.controller;

import constants.Message;
import constants.Router;
import constants.StatusCode;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import product.daos.ProductDao;
import utils.GetParam;
import product.dtos.Product;
import utils.Helper;

/**
 *
 * @author locnh
 */
@WebServlet(name = "ProductFilterController", urlPatterns = {"/" + Router.PRODUCT_FILTER_CONTROLLER})
public class ProductFilterController extends HttpServlet {

    protected boolean processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/html;charset=UTF-8");
        ProductDao productDao = new ProductDao();

        // get params
        String name = GetParam.getStringParam(request, "name", "Name", 0, 255, "");
        Float minPrice = GetParam.getFloatParams(request, "minPrice", "min price", 0, Float.MAX_VALUE, 0.0f);
        Float maxPrice = GetParam.getFloatParams(request, "maxPrice", "max price", 0, Float.MAX_VALUE, Float.MAX_VALUE);

        // check price
        if (minPrice > maxPrice) {
            request.setAttribute("priceError", Message.PRICE_ERROR_MESSAGE);
            return false;
        }

        // get products
        ArrayList<Product> products = productDao.getProducts(name, minPrice, maxPrice);

        // send products
        request.setAttribute("products", products);
        return true;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (!processRequest(request, response)) {
                // forward on 400
                request.getRequestDispatcher(Router.PRODUCTS_PAGE).forward(request, response);
                return;
            }
            // forward on 200
            request.getRequestDispatcher(Router.PRODUCTS_PAGE).forward(request, response);
        } catch (Exception e) {
            System.out.println(e);
            // forward on 500
            Helper.setAttribute(request, StatusCode.INTERNAL_SERVER_ERROR.getValue(),
                    Message.INTERNAL_ERROR_TITLE.getContent(),
                    Message.INTERNAL_ERROR_MESSAGE.getContent());
            request.getRequestDispatcher(Router.ERROR).forward(request, response);
        }
    }

}