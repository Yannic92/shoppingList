package de.yannicklem.shoppinglist.core;

import de.yannicklem.shoppinglist.core.user.entity.SLAuthority;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * @author  Yannic Klem - klem@synyx.de
 */
@ConfigurationProperties(prefix = "shopping-list.security")
public class ShoppingListSecurityProperties extends SecurityProperties {

    private int minimalPasswordLength = 8;

    private Admin admin;

    public Admin getAdmin() {

        return admin;
    }


    public void setAdmin(Admin admin) {

        this.admin = admin;
    }


    public int getMinimalPasswordLength() {

        return minimalPasswordLength;
    }


    public void setMinimalPasswordLength(int minimalPasswordLength) {

        this.minimalPasswordLength = minimalPasswordLength;
    }

    public static class Admin {

        /**
         * User name of the initial admin.
         */
        private String name = "admin";

        private String mail = "admin@einkaufsliste.yannic-klem.de";

        /**
         * Password for the initial admin.
         */
        private String password = UUID.randomUUID().toString();

        /**
         * Granted roles for the initial admin.
         */
        private List<String> role = new ArrayList<>(Arrays.asList(SLAuthority.ADMIN, SLAuthority.USER));

        private boolean defaultPassword = true;

        public String getName() {

            return this.name;
        }


        public void setName(String name) {

            this.name = name;
        }


        public String getPassword() {

            return this.password;
        }


        public void setPassword(String password) {

            if (password.startsWith("${") && password.endsWith("}") || !StringUtils.hasLength(password)) {
                return;
            }

            this.defaultPassword = false;
            this.password = password;
        }


        public List<String> getRole() {

            return this.role;
        }


        public void setRole(List<String> role) {

            this.role = new ArrayList<>(role);
        }


        public boolean isDefaultPassword() {

            return this.defaultPassword;
        }


        public String getMail() {

            return mail;
        }


        public void setMail(String mail) {

            this.mail = mail;
        }
    }
}
