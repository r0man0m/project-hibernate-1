package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties prop = new Properties();
        prop.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        prop.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        prop.put(Environment.USER, "root");
        prop.put(Environment.PASS, "root");
        prop.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5InnoDBDialect");
        prop.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Player.class)
                .addProperties(prop)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try(Session session = sessionFactory.openSession()) {
            NativeQuery<Player> query = session.createNativeQuery("select * from player", Player.class);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    @Override
    public int getAllCount() {
        try(Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createNamedQuery("player_getAllCount", Long.class);
            return Math.toIntExact(query.getSingleResult());
        }
    }

    @Override
    public Player save(Player player) {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(player);
            tx.commit();
            return player;
        }
    }

    @Override
    public Player update(Player player) {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(player);
            tx.commit();
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try(Session session = sessionFactory.openSession()) {
            Player player = session.find(Player.class, id);
            return Optional.of(player);
        }
    }

    @Override
    public void delete(Player player) {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(player);
            tx.commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}