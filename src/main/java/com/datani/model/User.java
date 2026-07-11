package com.datani.model;

/**
 * Representasi akun pengguna untuk keperluan login (autentikasi).
 * <p>
 * Sesuai SDD, entitas ini memiliki: id, nik, password, role.
 * <p>
 * Aplikasi mendukung dua cara login yang berbeda tergantung peran:
 * <ul>
 *     <li>{@link Role#PETANI} login menggunakan {@code nik}.</li>
 *     <li>{@link Role#PETUGAS_BPP} login menggunakan {@code username}
 *     (BUKAN NIK - Petugas BPP tidak memiliki NIK yang tercatat di
 *     aplikasi ini).</li>
 * </ul>
 * Field {@code username}, {@code fullName}, {@code email}, dan
 * {@code phone} ditambahkan secara aditif (tidak mengubah field inti di
 * atas) khusus untuk menampung data akun Petugas BPP, karena Petugas BPP
 * tidak memiliki entitas {@link Petani} tersendiri namun tetap perlu
 * menampilkan halaman "Profil Petugas BPP" (Nama Lengkap, Username,
 * Email, Nomor HP).
 * Untuk akun dengan role {@link Role#PETANI}, field data pribadi lengkap
 * disimpan pada entitas {@link Petani} yang terhubung melalui NIK yang sama.
 */
public class User {

    private int id;
    private String nik;
    private String password;
    private Role role;

    // --- Field tambahan (khusus dipakai login & Profil Petugas BPP) ---
    private String username;
    private String fullName;
    private String email;
    private String phone;

    public User() {
    }

    public User(int id, String nik, String password, Role role) {
        this.id = id;
        this.nik = nik;
        this.password = password;
        this.role = role;
    }

    public User(int id, String nik, String password, Role role,
                String fullName, String email, String phone) {
        this(id, nik, password, role);
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Konstruktor khusus akun Petugas BPP: login menggunakan
     * {@code username}, BUKAN NIK. Field {@code nik} tetap null untuk
     * akun jenis ini karena Petugas BPP tidak memiliki NIK dalam sistem.
     */
    public static User petugas(int id, String username, String password,
                                String fullName, String email, String phone) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.password = password;
        user.role = Role.PETUGAS_BPP;
        user.fullName = fullName;
        user.email = email;
        user.phone = phone;
        return user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", nik='" + nik + "', role=" + role + "}";
    }
}
