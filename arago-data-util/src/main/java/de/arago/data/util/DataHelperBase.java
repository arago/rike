/**
 * Copyright (c) 2010 arago AG, http://www.arago.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.arago.data.util;

import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class DataHelperBase<T> {
    private static final ConcurrentMap<String, SessionFactory> factories = new ConcurrentHashMap<String, SessionFactory>();

    private Stack<Transaction> transactions = new Stack<Transaction>();
    private final Class<?> klass;
    private Session listSession;
    private final SessionFactory factory;

    private static SessionFactory getFactory(String datasource, Properties additional) {
        datasource						 = datasource.replaceAll("\\W+", "");
        SessionFactory factory = factories.get(datasource);

        if (factory == null) {
            factory = ConfigHelper.makeFactory(datasource, additional);

            factories.putIfAbsent(datasource, factory);
        }

        return factory;
    }

    protected DataHelperBase(Class<?> klass, String datasource, Properties p) {
        this.klass   = klass;
        this.factory = getFactory(datasource, p);
    }

    private Session session() {
        return factory.getCurrentSession();
    }

    private Session listSession() {
        if (listSession == null) {
            listSession = factory.openSession();
        }

        return listSession;
    }

    private void begin() {
        begin(session());
    }

    private void begin(Session session) {
        Transaction transaction = session.beginTransaction();
        transaction.begin();
        transactions.push(transaction);
    }

    private void commit() {
        if (!transactions.isEmpty()) {
            transactions.pop().commit();
        }
    }

    private void rollback() {
        if (!transactions.isEmpty()) {
            transactions.pop().rollback();
        }
    }

    public Criteria filter() {
        return listSession().createCriteria(klass);
    }

    public List<T> list() {
        return list(filter());
    }

    public SQLQuery createSQLQuery(String query) {
        begin();
        return session().createSQLQuery(query);
    }

    public Query createQuery(String query) {
        begin();
        return session().createQuery(query);
    }

    public Query execute(Query query) {
        query.executeUpdate();
        commit();

        return query;
    }

    public List<Object> list(Query q) {
        return q.list();
    }

    public void finish(Query q) {
        session().disconnect();
    }

    public List<T> list(Criteria crit) {
        Session currentSession = null;

        try {
            currentSession = listSession();
            begin(currentSession);
            List<T> result = crit.list();
            commit();

            return result;
        } catch (Exception ex) {

            rollback();
            throw new RuntimeException(ex);
        } finally {
            listSession = null;

            if (currentSession != null) {
                currentSession.close();
            }
        }
    }

    public T find(Criteria crit) {
        List<T> list = list(crit);

        if (list == null || list.isEmpty()) return null;

        return list.get(0);
    }


    public T find(String id) {
        return find(Long.valueOf(id));
    }

    public T find(long id) {
        try {
            Object who = klass.newInstance();

            begin();

            session().load(who, id);

            commit();

            return (T) who;
        } catch (Exception ex) {
            rollback();
            throw new RuntimeException(ex);
        } finally {
            session().disconnect();
        }
    }

    public T save(T instance) {
        try {
            begin();

            session().saveOrUpdate(instance);

            commit();

            return instance;
        } catch (Exception ex) {
            rollback();
            throw new RuntimeException(ex);
        } finally {
            session().disconnect();
        }
    }

    public void kill(List<T> list) {
        try {
            begin();

            for (T instance : list) {
                _kill(instance);
            }

            commit();
        } catch (Exception ex) {
            rollback();
            throw new RuntimeException(ex);
        } finally {
            session().disconnect();
        }
    }

    public void kill(T instance) {
        try {
            begin();

            _kill(instance);

            commit();
        } catch (Exception ex) {
            rollback();
            throw new RuntimeException(ex);
        } finally {
            session().disconnect();
        }
    }

    private void _kill(T instance) {
        session().delete(instance);
    }
}
