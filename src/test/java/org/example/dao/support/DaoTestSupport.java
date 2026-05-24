package org.example.dao.support;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class DaoTestSupport {

    @Mock
    protected EntityManagerFactory emf;
    @Mock
    protected EntityManager em;
    @Mock
    protected EntityTransaction tx;

    @BeforeEach
    protected void setUpEntityManager() {
        lenient().when(emf.createEntityManager()).thenReturn(em);
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @SuppressWarnings("unchecked")
    protected <T> TypedQuery<T> mockNamedQuery(Class<T> type) {
        TypedQuery<T> query = mock(TypedQuery.class);
        when(em.createNamedQuery(anyString(), any(Class.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        return query;
    }

    @SuppressWarnings("unchecked")
    protected <T> TypedQuery<T> mockJpqlQuery(Class<T> type) {
        TypedQuery<T> query = mock(TypedQuery.class);
        when(em.createQuery(anyString(), any(Class.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        return query;
    }
}
