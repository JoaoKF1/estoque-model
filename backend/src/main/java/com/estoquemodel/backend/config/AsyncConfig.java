package com.estoquemodel.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Habilita o @Async e cria a pool de threads usada para enviar e-mail fora da
 * requisição HTTP. Sem isso, o cadastro ficaria travado esperando o SMTP
 * responder (até 5s de timeout por etapa, conforme application.properties).
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean("emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);

        // Fila limitada de propósito: se o SMTP ficar fora do ar e os envios
        // acumularem, o executor recusa a tarefa (RejectedExecutionException)
        // em vez de crescer sem limite e consumir a memória do servidor. Quem
        // chamou trata essa recusa - ver AuthService.enviarNovoCodigoConfirmacao.
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("email-");

        // No shutdown, espera os e-mails que já estão saindo terminarem,
        // para não perder uma mensagem no meio do caminho.
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(20);

        executor.initialize();
        return executor;
    }

    /**
     * Métodos @Async que retornam void não têm quem receba a exceção: sem este
     * handler, uma falha de envio desapareceria sem deixar rastro no log.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                log.error("Falha na tarefa assíncrona {}: {}", method.getName(), ex.getMessage());
    }
}
