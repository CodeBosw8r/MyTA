package myta.message.model;

public class EmailAddress {

    private String email;

    private String name;

    public EmailAddress(String email, String name) {

        /*
         * if (name == null) {
         * 
         * $ltPos = strpos(email, '<'); $gtPos = ($ltPos !== false) ?
         * strpos(email, '>', strpos(email, '<')) : false;
         * 
         * if (($ltPos !== false) && ($gtPos !== false)) {
         * 
         * name = trim(substr(email, 0, $ltPos - 1)); email = trim(substr(email,
         * $ltPos + 1, ($gtPos - $ltPos) - 1));
         * 
         * }
         * 
         * }
         * 
         * if (name != '') {
         * 
         * name = trim(name);
         * 
         * if ((substr(name, 0, 1) == '"') && (substr(name, -1) == '"')) {
         * 
         * $cleanName = trim(substr(name, 1, -1)); name = $cleanName;
         * 
         * }
         * 
         * }
         * 
         * $this->email = email; $this->name = name;
         * 
         */
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
