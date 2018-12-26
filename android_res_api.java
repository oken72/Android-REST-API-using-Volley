private void uploadBitmap(final Bitmap bitmap) {

        mTextView = (TextView) findViewById(R.id.textView1);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, SERVER_POST_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            String[] predictions = obj.getString("predictions").split(",");
                            int length = predictions.length;
                            String countString = predictions[length -1];
                            String countValue = countString.substring(countString.indexOf(':') + 1, countString.indexOf('}'));
                            mTextView.setText("Box Count: " + countValue);
                            Log.d("Response", obj.getString("success"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Response", error.getMessage());
            }
        }){

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                //String credentials = "cairo-dina:iman2dina";
                //String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "multipart/form-data");
                // headers.put("Authorization", auth);
                return headers;
            }

            /*
             *   Here we are passing image by renaming it with a unique name
             */
            @Override
            protected Map<String, DataPart> getByteData(){
                Map<String, DataPart> params = new HashMap<>();
                String imagename = mCurrentPhotoPath;
                params.put("image", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }

        };

        // Adding the request to Volley
        Volley.newRequestQueue(MainActivity.this).add(volleyMultipartRequest);
    }
