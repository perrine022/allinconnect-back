package com.allinconnect.allinconnectback2.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "monthly_stats")
public class MonthlyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;
    private int month;
    private long activeUsers; // Utilisateurs avec abonnement
    private long totalUsers;  // Nombre total d'utilisateurs
    private double revenue;   // Total des paiements reçus

    public MonthlyStat() {}

    public MonthlyStat(int year, int month, long activeUsers, long totalUsers, double revenue) {
        this.year = year;
        this.month = month;
        this.activeUsers = activeUsers;
        this.totalUsers = totalUsers;
        this.revenue = revenue;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }
}
