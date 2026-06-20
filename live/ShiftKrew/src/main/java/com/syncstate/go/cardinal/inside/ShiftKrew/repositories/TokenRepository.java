package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.enums.TokenType;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface TokenRepository extends JpaRepository<Token, BigInteger> {

    @Query("Select tp from Token tp WHERE tp.deletedAt IS NULL AND " +
            "tp.tokenOwnedByEntityId = :tokenOwnedByEntityId AND " +
            "tp.usedAt IS NULL AND tp.token = :token AND " +
            "tp.tokenType = :tokenType AND " +
            "tp.expiredAt > CURRENT_TIMESTAMP")
    public Token getValidToken(BigInteger tokenOwnedByEntityId, String token, TokenType tokenType);


    @Query("Select tp from Token tp where tp.token = :token AND " +
            "tp.data = data AND tp.tokenType = :tokenType")
    public Token validateToken(String token, String data, TokenType tokenType);

}
