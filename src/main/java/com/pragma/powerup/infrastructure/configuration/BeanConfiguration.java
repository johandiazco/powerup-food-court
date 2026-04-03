package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IAuthenticationService;
import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.api.IEmployeeEfficiencyServicePort;
import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.api.IOrderTraceServicePort;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.spi.IAuthenticationPort;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IEfficiencyMetricsPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IOrderTracePersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.usecase.AuthenticationUseCase;
import com.pragma.powerup.domain.usecase.DishUseCase;
import com.pragma.powerup.domain.usecase.EmployeeEfficiencyUseCase;
import com.pragma.powerup.domain.usecase.OrderTraceUseCase;
import com.pragma.powerup.domain.usecase.OrderUseCase;
import com.pragma.powerup.domain.usecase.RestaurantUseCase;
import com.pragma.powerup.domain.usecase.UserUseCase;
import com.pragma.powerup.infrastructure.out.jpa.adapter.CategoryJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.EfficiencyMetricsJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UserJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.ICategoryRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;
    private final ICategoryRepository categoryRepository;
    private final IUserRepository userRepository;
    private final IUserEntityMapper userEntityMapper;
    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    // PERSISTENCE PORTS
    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public ICategoryPersistencePort categoryPersistencePort() {
        return new CategoryJpaAdapter(categoryRepository);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IUserPersistencePort userPersistencePort() {
        return new UserJpaAdapter(userRepository, userEntityMapper);
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderEntityMapper);
    }

    @Bean
    public IEfficiencyMetricsPersistencePort efficiencyMetricsPersistencePort() {
        return new EfficiencyMetricsJpaAdapter(orderRepository);
    }

    // SERVICE PORTS (UseCases)
    @Bean
    public IRestaurantServicePort restaurantServicePort() {
        // CORREGIDO: Ahora recibe userPersistencePort para validar rol del owner (HU-2)
        return new RestaurantUseCase(restaurantPersistencePort(), userPersistencePort());
    }

    @Bean
    public IDishServicePort dishServicePort() {
        return new DishUseCase(
                dishPersistencePort(),
                restaurantPersistencePort(),
                categoryPersistencePort()
        );
    }

    @Bean
    public IUserServicePort userServicePort() {
        return new UserUseCase(userPersistencePort());
    }

    @Bean
    public IAuthenticationService authenticationService(
            IAuthenticationPort authenticationPort) {
        return new AuthenticationUseCase(
                authenticationPort,
                userPersistencePort()
        );
    }

    @Bean
    public IOrderTraceServicePort orderTraceServicePort(
            IOrderTracePersistencePort tracePersistencePort) {
        return new OrderTraceUseCase(tracePersistencePort);
    }

    @Bean
    public IOrderServicePort orderServicePort(
            IOrderTraceServicePort traceServicePort) {
        return new OrderUseCase(
                orderPersistencePort(),
                restaurantPersistencePort(),
                dishPersistencePort(),
                traceServicePort,
                userPersistencePort()
        );
    }

    @Bean
    public IEmployeeEfficiencyServicePort employeeEfficiencyServicePort() {
        return new EmployeeEfficiencyUseCase(
                efficiencyMetricsPersistencePort(),
                userPersistencePort(),
                restaurantPersistencePort()
        );
    }
}