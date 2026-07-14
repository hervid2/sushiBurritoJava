package com.restaurante.app.navigation;

import com.restaurante.app.exception.DomainException;
import com.restaurante.app.models.User;
import com.restaurante.app.views.admin.AdminPanelView;
import com.restaurante.app.views.cocina.CocinaPanelView;
import com.restaurante.app.views.mesero.WaiterPanelView;

import org.springframework.stereotype.Component;

/**
 * Presentation-layer coordinator that opens the correct home screen for a signed-in user.
 *
 * <p>Keeping this navigation decision out of the service layer preserves the single-responsibility
 * split: services own business rules, this component owns which window to show for each role. New
 * roles are added here without touching authentication logic (open/closed principle).
 */
@Component
public class NavigationManager {

    /**
     * Opens the home window that matches the user's role.
     *
     * @param user the authenticated user
     * @throws DomainException if the user's role is not recognised
     */
    public void openHomeFor(User user) throws DomainException {
        switch (user.getRole().toLowerCase()) {
            case "administrador" -> new AdminPanelView().setVisible(true);
            case "mesero" -> new WaiterPanelView(user.getId()).setVisible(true);
            case "cocinero" -> new CocinaPanelView().setVisible(true);
            default -> throw new DomainException("Rol de usuario no reconocido.");
        }
    }
}
