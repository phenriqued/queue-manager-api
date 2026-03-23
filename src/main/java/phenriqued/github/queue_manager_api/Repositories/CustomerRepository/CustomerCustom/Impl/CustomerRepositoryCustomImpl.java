package phenriqued.github.queue_manager_api.Repositories.CustomerRepository.CustomerCustom.Impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import phenriqued.github.queue_manager_api.Models.Customer.CustomerEntity;
import phenriqued.github.queue_manager_api.Repositories.CustomerRepository.CustomerCustom.CustomerRepositoryCustom;
import phenriqued.github.queue_manager_api.Repositories.CustomerRepository.CustomerCustom.Param.CustomerFilterParams;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryCustomImpl implements CustomerRepositoryCustom {

    private EntityManager entityManager;

    public CustomerRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<CustomerEntity> getWithFilter(CustomerFilterParams params) {

        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerEntity> query = criteriaBuilder.createQuery(CustomerEntity.class);

        Root<CustomerEntity> customer = query.from(CustomerEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (params.getName() != null){
            predicates.add(criteriaBuilder.like(customer.get("name"), "%" + params.getName() + "%"));
        }
        if (params.getCpf() != null){
            predicates.add(criteriaBuilder.like(customer.get("cpf"), "%" + params.getCpf() + "%"));
        }
        if (params.getPhoneNumber() != null){
            predicates.add(criteriaBuilder.like(customer.get("phoneNumber"), "%" + params.getPhoneNumber() + "%"));
        }
        if (params.getBirthDate() != null){
            predicates.add(criteriaBuilder.equal(customer.get("birthDate"), params.getBirthDate()));
        }
        if (params.getIsPriority() != null){
            predicates.add(criteriaBuilder.equal(customer.get("isPriority"), params.getIsPriority()));
        }

        if (!predicates.isEmpty()){
            query.where(predicates.toArray(Predicate[]::new));
        }

        TypedQuery<CustomerEntity> queryResult = this.entityManager.createQuery(query);

        return queryResult.getResultList();
    }


}
