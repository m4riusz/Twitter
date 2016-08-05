package com.twitter.service;

import com.twitter.model.Result;
import com.twitter.model.UserVote;

/**
 * Created by mariusz on 05.08.16.
 */
public interface AbstractPostInterface {

    public Result<Boolean> vote(UserVote userVote);
}
